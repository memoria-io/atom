package io.memoria.atom.eventsourcing.aggregate;

import io.memoria.atom.eventsourcing.aggregate.store.Store;
import io.memoria.atom.eventsourcing.command.Command;
import io.memoria.atom.eventsourcing.command.exceptions.CommandException;
import io.memoria.atom.eventsourcing.event.Event;
import io.memoria.atom.eventsourcing.state.State;
import io.memoria.atom.eventsourcing.state.StateId;

import java.util.Optional;

class DefaultLocalAggregates implements LocalAggregates {
  private final Store store;
  private final AggregateFactory aggregateFactory;

  DefaultLocalAggregates(Store store, AggregateFactory aggregateFactory) {
    this.store = store;
    this.aggregateFactory = aggregateFactory;
  }

  @Override
  public Optional<Event> decide(StateId stateId, Command command) throws CommandException {
    store.computeIfAbsent(stateId, aggregateFactory::create);
    return store.get(stateId).decide(command);
  }

  @Override
  public Optional<State> evolve(StateId stateId, Event event) {
    store.computeIfAbsent(stateId, aggregateFactory::create);
    return store.get(stateId).evolve(event);
  }
}
