package com.marmoush.javakit.eventsourcing.aggregate;

import com.marmoush.javakit.eventsourcing.command.Command;
import com.marmoush.javakit.eventsourcing.command.exceptions.CommandException;
import com.marmoush.javakit.eventsourcing.event.Event;
import com.marmoush.javakit.eventsourcing.event.repo.EventRepo;
import com.marmoush.javakit.eventsourcing.state.StateId;

import java.util.Optional;

public interface Aggregate {
  StateId stateId();

  Optional<Event> handle(Command command) throws CommandException;

  static Aggregate create(StateId stateId, Decider decider, Evolver evolver, EventRepo eventRepo) {
    return new SyncAggregate(stateId, decider, evolver, eventRepo);
  }
}
