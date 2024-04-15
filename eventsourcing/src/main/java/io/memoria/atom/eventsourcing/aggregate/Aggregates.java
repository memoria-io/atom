package io.memoria.atom.eventsourcing.aggregate;

import io.memoria.atom.eventsourcing.aggregate.store.Store;
import io.memoria.atom.eventsourcing.command.Command;
import io.memoria.atom.eventsourcing.command.exceptions.CommandException;
import io.memoria.atom.eventsourcing.event.Event;
import io.memoria.atom.eventsourcing.state.StateId;

import java.util.Optional;

public class Aggregates {
  private final Store store;
  private final AggregateFactory aggregateFactory;

  private Aggregates(Store store, AggregateFactory aggregateFactory) {
    this.store = store;
    this.aggregateFactory = aggregateFactory;
  }

  public Optional<Event> handle(StateId stateId, Command command) throws CommandException {
    store.computeIfAbsent(stateId, aggregateFactory::create);
    return store.get(stateId).handle(command);
  }

  public static Aggregates create(Store store, AggregateFactory aggregateFactory) {
    return new Aggregates(store, aggregateFactory);
  }
}