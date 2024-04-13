package io.memoria.atom.eventsourcing.aggregate;

import io.memoria.atom.eventsourcing.aggregate.store.Store;
import io.memoria.atom.eventsourcing.command.Command;
import io.memoria.atom.eventsourcing.command.exceptions.CommandException;
import io.memoria.atom.eventsourcing.event.Event;
import io.memoria.atom.eventsourcing.state.State;
import io.memoria.atom.eventsourcing.state.StateId;

import java.util.Optional;

public interface LocalAggregates  {
  Optional<Event> decide(StateId stateId, Command command) throws CommandException;

  Optional<State> evolve(StateId stateId, Event event);

  static LocalAggregates create(Store store, AggregateFactory aggregateFactory) {
    return new DefaultLocalAggregates(store, aggregateFactory);
  }
}
