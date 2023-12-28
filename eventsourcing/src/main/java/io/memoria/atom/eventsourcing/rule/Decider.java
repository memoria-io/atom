package io.memoria.atom.eventsourcing.rule;

import io.memoria.atom.core.id.Id;
import io.memoria.atom.eventsourcing.Command;
import io.memoria.atom.eventsourcing.Event;
import io.memoria.atom.eventsourcing.EventId;
import io.memoria.atom.eventsourcing.EventMeta;
import io.memoria.atom.eventsourcing.State;
import io.memoria.atom.eventsourcing.exceptions.MismatchingState;
import io.vavr.Function2;
import io.vavr.control.Try;

import java.util.function.Supplier;

public interface Decider extends Function2<State, Command, Try<Event>> {
  Supplier<Id> idSupplier();

  Supplier<Long> timeSupplier();

  Try<Event> apply(Command c);

  default Try<EventMeta> eventMeta(Command cmd) {
    var meta = new EventMeta(EventId.of(idSupplier().get()),
                             0,
                             cmd.meta().stateId(),
                             cmd.meta().commandId(),
                             timeSupplier().get(),
                             cmd.meta().sagaSource());
    return Try.success(meta);
  }

  default Try<EventMeta> eventMeta(State state, Command cmd) {
    if (state.meta().stateId().equals(cmd.meta().stateId())) {
      var meta = new EventMeta(EventId.of(idSupplier().get()),
                               state.meta().version() + 1,
                               state.meta().stateId(),
                               cmd.meta().commandId(),
                               timeSupplier().get(),
                               cmd.meta().sagaSource());
      return Try.success(meta);
    } else {
      return Try.failure(MismatchingState.stateId(cmd, state));
    }
  }
}
