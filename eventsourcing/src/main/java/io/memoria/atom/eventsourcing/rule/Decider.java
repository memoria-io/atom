package io.memoria.atom.eventsourcing.rule;

import io.memoria.atom.core.id.Id;
import io.memoria.atom.eventsourcing.Command;
import io.memoria.atom.eventsourcing.ESException.MismatchingStateId;
import io.memoria.atom.eventsourcing.Event;
import io.memoria.atom.eventsourcing.EventId;
import io.memoria.atom.eventsourcing.EventMeta;
import io.memoria.atom.eventsourcing.State;
import io.memoria.atom.eventsourcing.StateId;
import io.vavr.Function2;
import io.vavr.control.Try;

import java.util.function.Supplier;

public interface Decider<S extends State, C extends Command, E extends Event> extends Function2<S, C, Try<E>> {
  Supplier<Id> idSupplier();

  Supplier<Long> timeSupplier();

  Try<E> apply(C c);

  default Try<EventMeta> eventMeta(C cmd) {
    var meta = new EventMeta(EventId.of(idSupplier().get()),
                             0,
                             StateId.of(cmd.meta().shardKey()),
                             cmd.meta().commandId(),
                             timeSupplier().get(),
                             cmd.meta().sagaSource());
    return Try.success(meta);
  }

  default Try<EventMeta> eventMeta(S state, C cmd) {
    if (state.meta().shardKey().equals(cmd.meta().shardKey())) {
      var meta = new EventMeta(EventId.of(idSupplier().get()),
                               state.meta().version() + 1,
                               StateId.of(cmd.meta().shardKey()),
                               cmd.meta().commandId(),
                               timeSupplier().get(),
                               cmd.meta().sagaSource());
      return Try.success(meta);
    } else {
      return Try.failure(MismatchingStateId.of(state.meta().stateId(), cmd.meta().stateId()));
    }
  }
}
