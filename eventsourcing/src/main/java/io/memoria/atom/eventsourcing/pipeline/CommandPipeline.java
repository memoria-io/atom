package io.memoria.atom.eventsourcing.pipeline;

import io.memoria.atom.core.id.Id;
import io.memoria.atom.core.repo.KVStore;
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
  public final CommandRoute commandRoute;
  // Infra
  private final CommandStream<C> commandStream;
  private final EventStream<E> eventStream;
  private final KVStore kvStore;
  private final String kvStoreKey;
  // In memory
  private final Set<Id> processedCommands;
  private final Set<Id> processedEvents;
  private final Map<Id, S> aggregates;

  public CommandPipeline(Domain<S, C, E> domain,
                         CommandRoute commandRoute,
                         ESMsgStream esMsgStream,
                         KVStore kvStore,
                         TextTransformer transformer) {
    // Core
    this.domain = domain;
    this.commandRoute = commandRoute;
    // Infra
    this.commandStream = CommandStream.create(commandRoute, esMsgStream, transformer, domain.cClass());
    this.eventStream = EventStream.create(commandRoute.eventTopic(),
                                          commandRoute.eventTopicPartition(),
                                          esMsgStream,
                                          transformer,
                                          domain.eClass());
    this.kvStore = kvStore;
    this.kvStoreKey = commandRoute.eventTopic() + commandRoute.eventTopicPartition();
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
               .map(this::evolve) // evolve in memory
               .flatMap(this::storeLastEventId) // then store latest eventId even if possibly not persisted
               .flatMap(eventStream::pub) // publish event
               .flatMap(this::saga); // publish a command based on such event
  }

  private Mono<E> storeLastEventId(E e) {
    return kvStore.set(this.kvStoreKey, e.eventId().value()).map(k -> e);
  }

  /**
   * Used internally for lazy loading, and can be called for warmups
   */
  public Flux<E> init(Id stateId) {
    if (this.aggregates.containsKey(stateId)) {
      return Flux.empty();
    } else {
      return kvStore.get(this.kvStoreKey).map(Id::of).flatMapMany(eventStream::subUntil).map(this::evolve);
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