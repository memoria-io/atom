package com.marmoush.atom.eventsourcing.aggregate;

import com.marmoush.atom.eventsourcing.command.Command;
import com.marmoush.atom.eventsourcing.command.exceptions.CommandException;
import com.marmoush.atom.eventsourcing.event.Event;
import com.marmoush.atom.eventsourcing.event.repo.EventRepo;
import com.marmoush.atom.eventsourcing.state.StateId;

import java.util.Optional;

public interface Aggregate {
  StateId stateId();

  Optional<Event> handle(Command command) throws CommandException;

  static Aggregate create(StateId stateId, Decider decider, Evolver evolver, EventRepo eventRepo) {
    return new SyncAggregate(stateId, decider, evolver, eventRepo);
  }
}
