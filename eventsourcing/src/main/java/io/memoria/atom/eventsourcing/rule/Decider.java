package io.memoria.atom.eventsourcing.rule;

import io.memoria.atom.core.id.Id;
import io.memoria.atom.eventsourcing.command.Command;
import io.memoria.atom.eventsourcing.command.exceptions.MismatchingCommandState;
import io.memoria.atom.eventsourcing.event.Event;
import io.memoria.atom.eventsourcing.event.EventId;
import io.memoria.atom.eventsourcing.event.EventMeta;
import io.memoria.atom.eventsourcing.state.State;

import java.util.function.Supplier;

public interface Decider {
  Supplier<Id> idSupplier();

  Supplier<Long> timeSupplier();

  /**
   * @param command   the incoming command
   * @param eventMeta the new EventMeta
   * @return a new Event with eventMeta as its value
   */
  Event createBy(Command command, EventMeta eventMeta);

  /**
   * @param state     the initial state
   * @param command   the incoming command
   * @param eventMeta the new EventMeta
   * @return a new Event with eventMeta as its value
   */
  Event decide(State state, Command command, EventMeta eventMeta) throws Exception;

  default Event apply(Command command) {
    return createBy(command, eventMeta(command));
  }

  default Event apply(State state, Command command) throws Exception {
    return decide(state, command, eventMeta(state, command));
  }

  default EventMeta eventMeta(Command cmd) {
    return new EventMeta(EventId.of(idSupplier().get()),
                         0,
                         cmd.meta().stateId(),
                         cmd.meta().commandId(),
                         timeSupplier().get(),
                         cmd.meta().sagaSource().orElse(null));
  }

  default EventMeta eventMeta(State state, Command cmd) {
    if (state.meta().stateId().equals(cmd.meta().stateId())) {
      return new EventMeta(EventId.of(idSupplier().get()),
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
