package io.memoria.atom.eventsourcing.rule;

import io.memoria.atom.core.id.Id;
import io.memoria.atom.eventsourcing.ESCallable;
import io.memoria.atom.eventsourcing.ESException;
import io.memoria.atom.eventsourcing.command.Command;
import io.memoria.atom.eventsourcing.command.exceptions.MismatchingCommandState;
import io.memoria.atom.eventsourcing.event.Event;
import io.memoria.atom.eventsourcing.event.EventId;
import io.memoria.atom.eventsourcing.event.EventMeta;
import io.memoria.atom.eventsourcing.state.State;

import java.util.function.Function;
import java.util.function.Supplier;

public interface Decider {
  Supplier<Id> idSupplier();

  Supplier<Long> timeSupplier();

  Function<EventMeta, Event> createBy(Command command);

  Function<EventMeta, ESCallable<Event>> decide(State state, Command command) throws ESException;

  default Event apply(Command command) {
    return createBy(command).apply(eventMeta(command));
  }

  default Event apply(State state, Command command) throws ESException {
    return decide(state, command).apply(eventMeta(state, command)).call();
  }

  default EventMeta eventMeta(Command cmd) {
    return new EventMeta(EventId.of(idSupplier().get()),
                         0,
                         cmd.meta().stateId(),
                         cmd.meta().commandId(),
                         timeSupplier().get(),
                         cmd.meta().sagaSource());
  }

  default EventMeta eventMeta(State state, Command cmd) {
    if (state.meta().stateId().equals(cmd.meta().stateId())) {
      return new EventMeta(EventId.of(idSupplier().get()),
                           state.meta().version() + 1,
                           state.meta().stateId(),
                           cmd.meta().commandId(),
                           timeSupplier().get(),
                           cmd.meta().sagaSource());
    } else {
      throw MismatchingCommandState.of(state.meta().stateId(), cmd);
    }
  }
}
