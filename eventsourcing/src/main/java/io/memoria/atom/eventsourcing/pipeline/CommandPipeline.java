package io.memoria.atom.eventsourcing.pipeline;

import io.memoria.atom.core.repo.ESRowRepo;
import io.memoria.atom.core.stream.ESMsgStream;
import io.memoria.atom.core.text.TextTransformer;
import io.memoria.atom.core.vavr.ReactorVavrUtils;
import io.memoria.atom.eventsourcing.*;
import io.memoria.atom.eventsourcing.repo.EventRepo;
import io.memoria.atom.eventsourcing.stream.CommandStream;
import io.memoria.atom.eventsourcing.stream.EventStream;
import io.vavr.control.Option;
import io.vavr.control.Try;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.*;

public class CommandPipeline<S extends State, C extends Command, E extends Event> {
  // Core
  public final Domain<S, C, E> domain;
  public final CommandRoute CommandRoute;
  // Infra
  private final CommandStream<C> commandStream;
  private final EventStream<E> eventStream;
  private final EventRepo<E> eventRepo;
  // In memory
  private final Set<CommandId> processedCommands;
  private final Map<StateId, StateAggregate<S>> aggregates;

  public CommandPipeline(Domain<S, C, E> domain,
                         CommandRoute commandRoute,
                         ESMsgStream esMsgStream,
                         ESRowRepo esRowRepo,
                         TextTransformer transformer) {
    // Core
    this.domain = domain;
    this.CommandRoute = commandRoute;
    // Infra
    this.commandStream = CommandStream.create(commandRoute, esMsgStream, transformer, domain.cClass());
    this.eventStream = EventStream.create(commandRoute.eventTable(),
                                          commandRoute.eventTopicPartition(),
                                          esMsgStream,
                                          transformer,
                                          domain.eClass());
    this.eventRepo = EventRepo.create(commandRoute.eventTable(), esRowRepo, transformer, domain.eClass());
    // In memory
    this.processedCommands = new HashSet<>();
    this.aggregates = new HashMap<>();
  }

  public Flux<E> append(Flux<C> cmds) {
    return cmds.flatMap(cmd -> this.init(cmd.stateId()).map(i -> cmd))
               .map(this::handle)
               .filter(Option::isDefined)
               .map(Option::get)
               .flatMap(ReactorVavrUtils::toMono)
               .flatMap(this::saga)
               .flatMap(eventStream::pub);
  }

  /**
   * Used internally for lazy loading, and can be called for warmups
   */
  public Flux<S> init(StateId stateId) {
    if (this.aggregates.containsKey(stateId)) {
      return Flux.just(this.aggregates.get(stateId).getState());
    } else {
      return eventRepo.getAll(stateId).map(this::evolve).map(StateAggregate::getState);
    }
  }

  Option<Try<E>> handle(C cmd) {
    if (processedCommands.contains(cmd.commandId())) {
      return Option.none();
    }
    if (aggregates.containsKey(cmd.stateId())) {
      return Option.some(domain.decider().apply(aggregates.get(cmd.stateId()).getState(), cmd));
    } else {
      return Option.some(domain.decider().apply(cmd));
    }
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

  StateAggregate<S> evolve(E e) {
    S s;
    if (aggregates.containsKey(e.stateId())) {
      s = domain.evolver().apply(aggregates.get(e.stateId()).getState(), e);
      aggregates.get(e.stateId()).updateState(s);
    } else {
      aggregates.put(e.stateId(), StateAggregate.of(domain.evolver().apply(e)));
    }
    this.processedCommands.add(e.commandId());
    return aggregates.get(e.stateId());
  }
}