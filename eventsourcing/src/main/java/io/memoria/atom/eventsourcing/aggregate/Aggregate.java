package io.memoria.atom.eventsourcing.aggregate;

import io.memoria.atom.eventsourcing.command.Command;
import io.memoria.atom.eventsourcing.command.exceptions.CommandException;
import io.memoria.atom.eventsourcing.event.Event;
import io.memoria.atom.eventsourcing.rule.Decider;
import io.memoria.atom.eventsourcing.rule.Evolver;
import io.memoria.atom.eventsourcing.state.State;
import io.memoria.atom.eventsourcing.state.StateId;

import java.util.Optional;

public interface Aggregate {
  StateId stateId();

  Optional<Event> decide(Command command) throws CommandException;

  Optional<State> evolve(Event event);

  static Aggregate create(Decider decider, Evolver evolver, StateId stateId) {
    return new DefaultAggregate(decider, evolver, stateId);
  }
}
