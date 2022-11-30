package io.memoria.atom.active.eventsourcing.pipeline;

import io.memoria.atom.active.eventsourcing.exception.PipelineException.MismatchingStateId;
import io.memoria.atom.core.eventsourcing.*;
import io.vavr.control.Try;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.BlockingDeque;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Stream;

class StatePipeline<S extends State, C extends Command, E extends Event> implements Pipeline<S, C> {
  private static final Logger log = LogManager.getLogger(StatePipeline.class.getSimpleName());
  // Business Rules
  private final Domain<S, C, E> domain;
  // In memory
  private S state;
  private final Set<CommandId> processed;
  private final BlockingDeque<C> cmdQueue;
  private final AtomicInteger eventSeqId;
  // Infra
  private final Route route;
  private final CommandStream<C> commandStream;
  private final EventRepo<E> eventRepo;

  public StatePipeline(Domain<S, C, E> domain, Route route, CommandStream<C> commandStream, EventRepo<E> eventRepo) {
    this.domain = domain;
    // In memory
    this.state = domain.initState();
    this.route = route;
    this.processed = new HashSet<>();
    this.cmdQueue = new LinkedBlockingDeque<>();
    this.eventSeqId = new AtomicInteger();
    // Infra
    this.commandStream = commandStream;
    this.eventRepo = eventRepo;
  }

  @Override
  public Try<Boolean> offer(C cmd) {
    if (isValidCommand(cmd)) {
      log.info(cmd);
      return Try.success(this.cmdQueue.offer(cmd));
    } else {
      var e = MismatchingStateId.create(cmd.stateId(), state.stateId());
      log.error(e);
      return Try.failure(e);
    }
  }

  @Override
  public Stream<Try<S>> stream() {
    return Stream.generate(this::take).map(this::handle);
  }

  public Try<S> handle(C cmd) {
    if (processed.contains(cmd.commandId())) {
      // No change, command has already been processed
      return Try.success(state);
    } else {
      return decide(cmd);
    }
  }

  public Try<S> decide(C cmd) {
    if (state.equals(domain.initState())) {
      eventRepo.getAll(route.eventTopic(), cmd.stateId()).forEach(tr -> tr.map(this::evolve));
    }
    return domain.decider().apply(state, cmd).flatMap(this::saga).flatMap(this::append).map(this::evolve);
  }

  public Try<E> saga(E e) {
    var sagaCmd = domain.saga().apply(e);
    if (sagaCmd.isDefined()) {
      C cmd = sagaCmd.get();
      var newPartition = cmd.partition(route.totalCmdPartitions());
      return commandStream.pub(route.cmdTopic(), newPartition, cmd).map(c -> e);
    } else {
      return Try.success(e);
    }
  }

  public S evolve(E e) {
    this.state = domain.evolver().apply(this.state, e);
    this.processed.add(e.commandId());
    this.eventSeqId.incrementAndGet();
    return this.state;
  }

  public boolean isValidCommand(C cmd) {
    return cmd.stateId().equals(state.stateId()) || state.equals(domain.initState());
  }

  private Try<E> append(E e) {
    return eventRepo.append(route.eventTopic(), this.eventSeqId.get(), e).map(i -> e);
  }

  private C take() {
    try {
      return cmdQueue.take();
    } catch (InterruptedException e) {
      throw new RuntimeException(e);
    }
  }
}
