package io.memoria.atom.eventsourcing.aggregate;

import io.memoria.atom.eventsourcing.aggregate.store.AggregateStore;
import io.memoria.atom.eventsourcing.command.Command;
import io.memoria.atom.eventsourcing.command.exceptions.CommandException;
import io.memoria.atom.eventsourcing.event.Event;
import io.memoria.atom.eventsourcing.state.State;
import io.memoria.atom.eventsourcing.state.StateId;

import java.util.Optional;

class DefaultLocalAggregates implements LocalAggregates {
  private final AggregateStore aggregateStore;
  private final AggregateFactory aggregateFactory;

  DefaultLocalAggregates(AggregateStore aggregateStore, AggregateFactory aggregateFactory) {
    this.aggregateStore = aggregateStore;
    this.aggregateFactory = aggregateFactory;
  }

  @Override
  public Optional<State> evolve(StateId stateId, Event event) {
    aggregateStore.computeIfAbsent(stateId, aggregateFactory::create);
    return aggregateStore.get(stateId).evolve(event);
  }

  @Override
  public Optional<Event> decide(StateId stateId, Command command) throws CommandException {
    aggregateStore.computeIfAbsent(stateId, aggregateFactory::create);
    return aggregateStore.get(stateId).decide(command);
  }
}
