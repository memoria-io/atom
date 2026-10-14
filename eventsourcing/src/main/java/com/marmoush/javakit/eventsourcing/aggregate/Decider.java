package com.marmoush.javakit.eventsourcing.aggregate;

import com.marmoush.javakit.core.id.Id;
import com.marmoush.javakit.eventsourcing.command.Command;
import com.marmoush.javakit.eventsourcing.command.exceptions.CommandException;
import com.marmoush.javakit.eventsourcing.command.exceptions.MismatchingCommandState;
import com.marmoush.javakit.eventsourcing.event.Event;
import com.marmoush.javakit.eventsourcing.event.EventIds;
import com.marmoush.javakit.eventsourcing.event.EventMeta;
import com.marmoush.javakit.eventsourcing.state.State;

import java.util.function.Supplier;

public interface Decider {
  Supplier<Id> idSupplier();

  Supplier<Long> timeSupplier();

  /**
   * @param command   the incoming command
   * @param eventMeta the new EventMeta
   * @return a new Event with eventMeta as its value
   */
  Event decide(Command command, EventMeta eventMeta) throws CommandException;

  /**
   * @param state     the initial state
   * @param command   the incoming command
   * @param eventMeta the new EventMeta
   * @return a new Event with eventMeta as its value
   *
   * @throws CommandException when a checked ESException happens
   */
  Event decide(State state, Command command, EventMeta eventMeta) throws CommandException;

  default Event decide(Command command) throws CommandException {
    return decide(command, eventMeta(command));
  }

  default Event decide(State state, Command command) throws CommandException {
    return decide(state, command, eventMeta(state, command));
  }

  default EventMeta eventMeta(Command cmd) {
    return new EventMeta(EventIds.of(idSupplier().get()),
                         0,
                         cmd.meta().stateId(),
                         cmd.meta().commandId(),
                         timeSupplier().get(),
                         cmd.meta().sagaSource().orElse(null));
  }

  default EventMeta eventMeta(State state, Command cmd) {
    if (state.meta().stateId().equals(cmd.meta().stateId())) {
      return new EventMeta(EventIds.of(idSupplier().get()),
                           state.meta().version() + 1,
                           state.meta().stateId(),
                           cmd.meta().commandId(),
                           timeSupplier().get(),
                           cmd.meta().sagaSource().orElse(null));
    } else {
      throw MismatchingCommandState.of(state.meta().stateId(), cmd);
    }
  }
}
