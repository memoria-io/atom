package io.memoria.atom.eventsourcing.aggregate;

import io.memoria.atom.eventsourcing.command.Command;
import io.memoria.atom.eventsourcing.command.exceptions.CommandException;
import io.memoria.atom.eventsourcing.event.Event;
import io.memoria.atom.eventsourcing.event.repo.EventRepo;
import io.memoria.atom.eventsourcing.state.StateId;

import java.util.Optional;

public interface Aggregate {
  StateId stateId();

  Optional<Event> handle(Command command) throws CommandException;

  static Aggregate create(StateId stateId, Decider decider, Evolver evolver, EventRepo eventRepo) {
    return new SyncAggregate(stateId, decider, evolver, eventRepo);
  }
}
