package io.memoria.atom.eventsourcing.handler;

import io.memoria.atom.eventsourcing.command.Command;
import io.memoria.atom.eventsourcing.command.exceptions.CommandException;
import io.memoria.atom.eventsourcing.event.Event;
import io.memoria.atom.eventsourcing.event.repo.EventRepo;
import io.memoria.atom.eventsourcing.rule.Decider;
import io.memoria.atom.eventsourcing.rule.Evolver;
import io.memoria.atom.eventsourcing.state.StateId;

import java.util.Optional;

public interface CommandHandler {
  StateId stateId();

  Optional<Event> handle(Command command) throws CommandException;

  static CommandHandler create(StateId stateId, Decider decider, Evolver evolver, EventRepo eventRepo) {
    return new DefaultCommandHandler(stateId, decider, evolver, eventRepo);
  }
}
