package io.memoria.atom.active.eventsourcing.pipeline;

import io.memoria.atom.active.eventsourcing.exception.PipelineException.MismatchingStateId;
import io.memoria.atom.active.eventsourcing.repo.*;
import io.memoria.atom.core.eventsourcing.*;
import io.memoria.atom.core.text.TextTransformer;
import io.vavr.control.Try;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.BlockingDeque;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Stream;

public class StatePipeline<S extends State, C extends Command, E extends Event> implements Pipeline<S, C> {
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
  private final CmdStream cmdRepo;
  private final EventRepo eventRepo;
  private final TextTransformer transformer;

  public StatePipeline(Domain<S, C, E> domain,
                       Route route,
                       CmdStream cmdRepo,
                       EventRepo eventRepo,
                       TextTransformer transformer) {
    this.domain = domain;
    // In memory
    this.state = domain.initState();
    this.route = route;
    this.processed = new HashSet<>();
    this.cmdQueue = new LinkedBlockingDeque<>();
    this.eventSeqId = new AtomicInteger();
    // Infra
    this.cmdRepo = cmdRepo;
    this.eventRepo = eventRepo;
    this.transformer = transformer;
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
      eventRepo.getAll(route.eventTopic(), cmd.stateId())
               .map(m -> transformer.deserialize(m.value(), domain.eClass()))
               .forEach(tr -> tr.map(this::evolve));
    }
    return domain.decider().apply(state, cmd).flatMap(this::saga).flatMap(this::append).map(this::evolve);
  }

  public Try<E> saga(E e) {
    var sagaCmd = domain.saga().apply(e);
    if (sagaCmd.isDefined()) {
      C cmd = sagaCmd.get();
      return toMessage(cmd).flatMap(cmdRepo::pub).map(c -> e);
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
    return toMessage(e).map(eventRepo::append).map(m -> e);
  }

  private Try<EventMsg> toMessage(E e) {
    return transformer.serialize(e).map(v -> toMsg(e, v));
  }

  private Try<CmdMsg> toMessage(C c) {
    return transformer.serialize(c).map(v -> toMsg(c, v));
  }

  private CmdMsg toMsg(C c, String v) {
    var newPartition = c.partition(route.totalCmdPartitions());
    return CmdMsg.create(route.cmdTopic(), newPartition, c.commandId().value(), v);
  }

  private EventMsg toMsg(E e, String v) {
    return EventMsg.create(route.eventTopic(), e.stateId(), eventSeqId.get(), v);
  }

  private C take() {
    try {
      return cmdQueue.take();
    } catch (InterruptedException e) {
      throw new RuntimeException(e);
    }
  }
}
