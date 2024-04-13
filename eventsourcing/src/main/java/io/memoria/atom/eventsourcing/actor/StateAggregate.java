package io.memoria.atom.eventsourcing.actor;

import io.memoria.atom.eventsourcing.command.Command;
import io.memoria.atom.eventsourcing.command.exceptions.CommandException;
import io.memoria.atom.eventsourcing.event.Event;
import io.memoria.atom.eventsourcing.state.State;
import io.memoria.atom.eventsourcing.state.StateId;

import java.util.Optional;

public interface StateAggregate {
  StateId stateId();

  Optional<Event> decide(Command command) throws CommandException;

  Optional<State> evolve(Event event);
}
