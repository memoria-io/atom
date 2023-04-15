package io.memoria.atom.eventsourcing.pipeline;

import io.memoria.atom.core.stream.ESMsgStream;
import io.memoria.atom.core.text.TextTransformer;
import io.memoria.atom.core.vavr.ReactorVavrUtils;
import io.memoria.atom.eventsourcing.*;
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
  // In memory
  private final Set<CommandId> processedCommands;
  private final Set<EventId> processedEvents;
  private final Map<StateId, S> aggregates;

  public CommandPipeline(Domain<S, C, E> domain,
                         CommandRoute commandRoute,
                         ESMsgStream esMsgStream,
                         TextTransformer transformer) {
    // Core
    this.domain = domain;
    this.CommandRoute = commandRoute;
    // Infra
    this.commandStream = CommandStream.create(commandRoute, esMsgStream, transformer, domain.cClass());
    this.eventStream = EventStream.create(commandRoute.eventTopic(),
                                          commandRoute.eventTopicPartition(),
                                          esMsgStream,
                                          transformer,
                                          domain.eClass());
    // In memory
    this.processedCommands = new HashSet<>();
    this.processedEvents = new HashSet<>();
    this.aggregates = new HashMap<>();
  }

  public Flux<E> handle(Flux<C> cmds) {
    return cmds.flatMap(cmd -> this.init(cmd.stateId()).thenMany(Flux.just(cmd)))
               .map(this::handle)
               .filter(Option::isDefined)
               .map(Option::get)
               .flatMap(ReactorVavrUtils::toMono)
               .skipWhile(e -> this.processedEvents.contains(e.eventId()))
               .map(this::evolve)
               .flatMap(this::saga)
               .flatMap(eventStream::pub);
  }

  /**
   * Used internally for lazy loading, and can be called for warmups
   */
  public Flux<E> init(StateId stateId) {
    if (this.aggregates.containsKey(stateId)) {
      return Flux.empty();
    } else {
      return eventStream.getLast().flatMapMany(last -> eventStream.subUntil(last.eventId())).map(this::evolve);
    }
  }

  Option<Try<E>> handle(C cmd) {
    if (processedCommands.contains(cmd.commandId())) {
      return Option.none();
    }
    if (aggregates.containsKey(cmd.stateId())) {
      return Option.some(domain.decider().apply(aggregates.get(cmd.stateId()), cmd));
    } else {
      return Option.some(domain.decider().apply(cmd));
    }
  }

  Mono<E> saga(E e) {
    var sagaCmd = domain.saga().apply(e);
    if (!this.processedCommands.contains(e.commandId()) && sagaCmd.isDefined()) {
      C cmd = sagaCmd.get();
      return commandStream.pub(cmd).map(c -> e);
    } else {
      return Mono.just(e);
    }
  }

  E evolve(E e) {
    S newState;
    if (aggregates.containsKey(e.stateId())) {
      newState = domain.evolver().apply(aggregates.get(e.stateId()), e);
    } else {
      newState = domain.evolver().apply(e);
    }
    aggregates.put(e.stateId(), newState);
    this.processedCommands.add(e.commandId());
    this.processedEvents.add(e.eventId());
    return e;
  }
}