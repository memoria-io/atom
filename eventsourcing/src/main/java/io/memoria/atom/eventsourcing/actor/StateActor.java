package io.memoria.atom.eventsourcing.actor;

import io.memoria.atom.eventsourcing.command.Command;
import io.memoria.atom.eventsourcing.event.Event;
import io.memoria.atom.eventsourcing.state.State;
import io.memoria.atom.eventsourcing.state.StateId;


public interface StateActor {
  StateId stateId();

  Event decide(Command command);

  State evolve(StateId stateId, Event event);
}
