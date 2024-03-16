package io.memoria.atom.eventsourcing.rule;

import io.memoria.atom.core.id.Id;
import io.memoria.atom.eventsourcing.command.Command;
import io.memoria.atom.eventsourcing.command.exceptions.MismatchingCommandState;
import io.memoria.atom.eventsourcing.event.Event;
import io.memoria.atom.eventsourcing.event.EventId;
import io.memoria.atom.eventsourcing.event.EventMeta;
import io.memoria.atom.eventsourcing.state.State;

import java.util.function.BiFunction;
import java.util.function.Supplier;

public interface Decider extends BiFunction<State, Command, Event> {
  Supplier<Id> idSupplier();

  Supplier<Long> timeSupplier();

  Event apply(Command c);

  default EventMeta eventMeta(Command cmd) {
    return new EventMeta(EventId.of(idSupplier().get()),
                         0,
                         cmd.meta().stateId(),
                         cmd.meta().commandId(),
                         timeSupplier().get(),
                         cmd.meta().sagaSource());
  }

  default EventMeta eventMeta(State state, Command cmd) throws MismatchingCommandState {
    if (state.meta().stateId().equals(cmd.meta().stateId())) {
      return new EventMeta(EventId.of(idSupplier().get()),
                           state.meta().version() + 1,
                           state.meta().stateId(),
                           cmd.meta().commandId(),
                           timeSupplier().get(),
                           cmd.meta().sagaSource());
    } else {
      throw MismatchingCommandState.of(cmd, state);
    }
  }
}
