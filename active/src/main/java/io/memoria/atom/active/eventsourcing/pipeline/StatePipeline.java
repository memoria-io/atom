package io.memoria.atom.active.eventsourcing.pipeline;

import io.memoria.atom.active.eventsourcing.exception.PipelineException.MismatchingStateId;
import io.memoria.atom.core.eventsourcing.*;
import io.vavr.control.Option;
import io.vavr.control.Try;

import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.BlockingDeque;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Stream;

class StatePipeline<S extends State, C extends Command, E extends Event> implements Pipeline<S, C, E> {
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
  public Try<Void> append(C cmd) {
    if (isValidCommand(cmd)) {
      return Try.run(() -> this.cmdQueue.putLast(cmd));
    } else {
      var e = MismatchingStateId.create(cmd.stateId(), state.stateId());
      return Try.failure(e);
    }
  }

  @Override
  public Stream<Try<E>> stream() {
    return Stream.generate(this::take).map(this::handle).filter(Option::isDefined).map(Option::get);
  }

  public Option<Try<E>> handle(C cmd) {
    if (processed.contains(cmd.commandId())) {
      // No change, command has already been processed
      return Option.none();
    } else {
      if (state.equals(domain.initState())) {
        eventRepo.getAll(route.eventTopic(), cmd.stateId()).forEach(tr -> tr.map(this::evolve));
      }
      var e = domain.decider().apply(state, cmd).flatMap(this::saga).flatMap(this::append).map(this::evolve);
      return Option.some(e);
    }
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

  public E evolve(E e) {
    this.state = domain.evolver().apply(this.state, e);
    this.processed.add(e.commandId());
    this.eventSeqId.incrementAndGet();
    return e;
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
