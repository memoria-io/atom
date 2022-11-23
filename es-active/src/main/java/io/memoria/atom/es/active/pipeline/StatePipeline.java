package io.memoria.atom.es.active.pipeline;

import io.memoria.atom.es.active.exception.PipelineException.MismatchingStateId;
import io.memoria.atom.es.active.repo.CommandRepo;
import io.memoria.atom.es.active.repo.EventRepo;
import io.memoria.atom.core.eventsourcing.Command;
import io.memoria.atom.core.eventsourcing.CommandId;
import io.memoria.atom.core.eventsourcing.Event;
import io.memoria.atom.core.eventsourcing.State;
import io.vavr.control.Try;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.BlockingDeque;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.stream.Stream;

public class StatePipeline<S extends State, C extends Command, E extends Event> implements Pipeline<S, C, E> {
  private static final Logger log = LogManager.getLogger(StatePipeline.class.getSimpleName());
  // Business Rules
  private final Domain<S, C, E> domain;
  // In memory
  private S state;
  private final Set<CommandId> processed;
  private final BlockingDeque<C> cmdQueue;
  // Infra
  private final CommandRepo<C> cmdRepo;
  private final EventRepo<E> eventRepo;

  public StatePipeline(Domain<S, C, E> domain, CommandRepo<C> cmdRepo, EventRepo<E> eventRepo) {
    this.domain = domain;
    // In memory
    this.state = domain.initState();
    this.processed = new HashSet<>();
    this.cmdQueue = new LinkedBlockingDeque<>();
    // Infra
    this.cmdRepo = cmdRepo;
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
      eventRepo.get(cmd.stateId()).forEach(tr -> tr.map(this::evolve));
    }
    return domain.decider().apply(state, cmd).flatMap(this::saga).flatMap(eventRepo::push).map(this::evolve);
  }

  public Try<E> saga(E e) {
    var sagaCmd = domain.saga().apply(e);
    if (sagaCmd.isDefined()) {
      C cmd = sagaCmd.get();
      return cmdRepo.push(cmd).map(c -> e);
    } else {
      return Try.success(e);
    }
  }

  public S evolve(E e) {
    this.state = domain.evolver().apply(this.state, e);
    this.processed.add(e.commandId());
    return this.state;
  }

  public boolean isValidCommand(C cmd) {
    return cmd.stateId().equals(state.stateId()) || state.equals(domain.initState());
  }

  private C take() {
    try {
      return cmdQueue.take();
    } catch (InterruptedException e) {
      throw new RuntimeException(e);
    }
  }
}
