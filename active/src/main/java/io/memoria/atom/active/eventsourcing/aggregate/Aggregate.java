package io.memoria.atom.active.eventsourcing.aggregate;

import io.memoria.atom.active.eventsourcing.infra.repo.EventRepo;
import io.memoria.atom.active.eventsourcing.infra.stream.CommandStream;
import io.memoria.atom.active.eventsourcing.infra.repo.ESRepo;
import io.memoria.atom.active.eventsourcing.infra.stream.ESStream;
import io.memoria.atom.core.eventsourcing.*;
import io.memoria.atom.core.eventsourcing.exception.ESException.MismatchingStateId;
import io.memoria.atom.core.text.TextTransformer;
import io.vavr.control.Option;
import io.vavr.control.Try;

import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.BlockingDeque;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Stream;

/**
 * Creates active stateful aggregation
 */
public class Aggregate<S extends State, C extends Command, E extends Event> {
  // Core
  public final Domain<S, C, E> domain;
  public final Route route;
  // Infra
  private final CommandStream<C> commandStream;
  private final EventRepo<E> eventRepo;
  // In memory
  private final Set<CommandId> processed;
  private final BlockingDeque<C> cmdQueue;
  private final AtomicInteger eventSeqId;
  private S state;

  public Aggregate(Domain<S, C, E> domain, Route route, ESStream esStream, ESRepo esRepo, TextTransformer transformer) {
    // Core
    this.domain = domain;
    this.route = route;
    // Infra
    this.commandStream = CommandStream.create(route, esStream, transformer, domain.cClass());
    this.eventRepo = EventRepo.create(route, esRepo, transformer, domain.eClass());
    // In memory
    this.processed = new HashSet<>();
    this.cmdQueue = new LinkedBlockingDeque<>();
    this.eventSeqId = new AtomicInteger();
    this.state = null;
  }

  public Try<C> append(C cmd) {
    return Try.of(() -> {
      this.cmdQueue.putLast(cmd);
      return cmd;
    });
  }

  public Stream<Try<E>> stream(StateId stateId) {
    return Stream.concat(init(stateId), processQueue());
  }

  Stream<Try<E>> init(StateId stateId) {
    return Stream.concat(eventRepo.getFirst(stateId).map(eTry -> eTry.map(this::evolve)),
                         eventRepo.getAll(stateId).map(eTry -> eTry.map(this::evolve)));
  }

  Stream<Try<E>> processQueue() {
    return Stream.generate(this::take)
                 .map(this::handle)
                 .filter(Option::isDefined)
                 .map(Option::get)
                 .map(eTry -> eTry.flatMap(this::saga))
                 .map(eTry -> eTry.flatMap(this::appendEvent))
                 .map(eTry -> eTry.map(this::evolve));
  }

  Option<Try<E>> handle(C cmd) {
    if (processed.contains(cmd.commandId())) {
      return Option.none();
    }
    if (this.state == null) {
      return Option.some(domain.decider().apply(cmd));
    } else {
      if (this.state.stateId().equals(cmd.stateId())) {
        return Option.some(domain.decider().apply(this.state, cmd));
      } else {
        return Option.some(Try.failure(MismatchingStateId.of(this.state.stateId(), cmd.stateId())));
      }
    }
  }

  E evolve(E e) {
    if (this.state == null) {
      this.state = domain.evolver().apply(e);
    } else {
      this.state = domain.evolver().apply(this.state, e);
    }
    this.processed.add(e.commandId());
    this.eventSeqId.incrementAndGet();
    return e;
  }

  Try<E> saga(E e) {
    var sagaCmd = domain.saga().apply(e);
    if (sagaCmd.isDefined()) {
      C cmd = sagaCmd.get();
      return commandStream.pub(cmd).map(c -> e);
    } else {
      return Try.success(e);
    }
  }

  private Try<E> appendEvent(E e) {
    return eventRepo.append(this.eventSeqId.get(), e).map(i -> e);
  }

  /**
   * @return blocks until interrupted
   */
  private C take() {
    try {
      return cmdQueue.take();
    } catch (InterruptedException e) {
      throw new RuntimeException(e);
    }
  }
}
