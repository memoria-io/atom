package io.memoria.reactive.eventsourcing.aggregate;

import io.memoria.atom.core.eventsourcing.*;
import io.memoria.atom.core.eventsourcing.exception.ESException.MismatchingStateId;
import io.memoria.atom.core.eventsourcing.infra.CRoute;
import io.memoria.atom.core.text.TextTransformer;
import io.memoria.reactive.core.vavr.ReactorVavrUtils;
import io.memoria.reactive.eventsourcing.infra.repo.EventRepo;
import io.memoria.reactive.eventsourcing.infra.stream.CommandStream;
import io.memoria.reactive.eventsourcing.infra.repo.ESRepo;
import io.memoria.reactive.eventsourcing.infra.stream.ESStream;
import io.vavr.control.Option;
import io.vavr.control.Try;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.BlockingDeque;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Creates active stateful aggregation
 */
public class Aggregate<S extends State, C extends Command, E extends Event> {
  // Core
  public final Domain<S, C, E> domain;
  public final CRoute CRoute;
  // Infra
  private final CommandStream<C> commandStream;
  private final EventRepo<E> eventRepo;
  // In memory
  private final Set<CommandId> processed;
  private final BlockingDeque<C> cmdQueue;
  private final AtomicInteger eventSeqId;
  private S state;

  public Aggregate(Domain<S, C, E> domain, CRoute CRoute, ESStream esStream, ESRepo esRepo, TextTransformer transformer) {
    // Core
    this.domain = domain;
    this.CRoute = CRoute;
    // Infra
    this.commandStream = CommandStream.create(CRoute, esStream, transformer, domain.cClass());
    this.eventRepo = EventRepo.create(CRoute, esRepo, transformer, domain.eClass());
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

  public Flux<E> stream(StateId stateId) {
    return Flux.concat(init(stateId), processQueue());
  }

  Flux<E> init(StateId stateId) {
    return eventRepo.getAll(stateId).map(this::evolve);
  }

  Flux<E> processQueue() {
    return sub().map(this::handle)
                .filter(Option::isDefined)
                .map(Option::get)
                .flatMap(ReactorVavrUtils::toMono)
                .flatMap(this::saga)
                .flatMap(this::appendEvent)
                .map(this::evolve);
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

  Mono<E> saga(E e) {
    var sagaCmd = domain.saga().apply(e);
    if (sagaCmd.isDefined()) {
      C cmd = sagaCmd.get();
      return commandStream.pub(cmd).map(c -> e);
    } else {
      return Mono.just(e);
    }
  }

  private Mono<E> appendEvent(E e) {
    return eventRepo.append(this.eventSeqId.get(), e).map(i -> e);
  }

  private Flux<C> sub() {
    return Flux.generate(c -> {
      try {
        c.next(this.cmdQueue.take());
      } catch (InterruptedException e) {
        c.error(e);
      }
    });
  }
}

