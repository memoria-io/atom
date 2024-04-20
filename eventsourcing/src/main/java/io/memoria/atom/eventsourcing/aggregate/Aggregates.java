package io.memoria.atom.eventsourcing.aggregate;

import io.memoria.atom.eventsourcing.aggregate.store.AggregateStore;
import io.memoria.atom.eventsourcing.command.Command;
import io.memoria.atom.eventsourcing.command.exceptions.CommandException;
import io.memoria.atom.eventsourcing.event.Event;
import io.memoria.atom.eventsourcing.state.StateId;

import java.util.Optional;

public class Aggregates {
  private final AggregateStore aggregateStore;
  private final AggregateFactory aggregateFactory;

  private Aggregates(AggregateStore aggregateStore, AggregateFactory aggregateFactory) {
    this.aggregateStore = aggregateStore;
    this.aggregateFactory = aggregateFactory;
  }

  public Optional<Event> handle(StateId stateId, Command command) throws CommandException {
    aggregateStore.computeIfAbsent(stateId, aggregateFactory::create);
    return aggregateStore.get(stateId).handle(command);
  }

  public static Aggregates create(AggregateStore aggregateStore, AggregateFactory aggregateFactory) {
    return new Aggregates(aggregateStore, aggregateFactory);
  }
}
