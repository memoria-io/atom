package io.memoria.atom.eventsourcing.rule;

import io.memoria.atom.core.id.Id;
import io.memoria.atom.eventsourcing.Command;
import io.memoria.atom.eventsourcing.CommandId;
import io.memoria.atom.eventsourcing.CommandMeta;
import io.memoria.atom.eventsourcing.Event;
import io.memoria.atom.eventsourcing.EventId;
import io.memoria.atom.eventsourcing.StateId;
import io.vavr.Function1;
import io.vavr.control.Option;

import java.util.function.Supplier;

public interface Saga<E extends Event, C extends Command> extends Function1<E, Option<C>> {
  Supplier<Id> idSupplier();

  Supplier<Long> timeSupplier();

  default CommandMeta commandMeta(StateId stateId) {
    return new CommandMeta(CommandId.of(idSupplier().get()), stateId, timeSupplier().get());
  }

  default CommandMeta commandMeta(StateId stateId, EventId sagaSource) {
    return new CommandMeta(CommandId.of(idSupplier().get()), stateId, timeSupplier().get(), Option.some(sagaSource));
  }
}
