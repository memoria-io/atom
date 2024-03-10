package io.memoria.atom.eventsourcing.rule;

import io.memoria.atom.core.id.Id;
import io.memoria.atom.eventsourcing.command.Command;
import io.memoria.atom.eventsourcing.command.CommandId;
import io.memoria.atom.eventsourcing.command.CommandMeta;
import io.memoria.atom.eventsourcing.event.Event;
import io.memoria.atom.eventsourcing.event.EventId;
import io.memoria.atom.eventsourcing.state.StateId;
import io.vavr.Function1;
import io.vavr.control.Option;

import java.util.function.Supplier;

public interface Saga extends Function1<Event, Option<Command>> {
  Supplier<Id> idSupplier();

  Supplier<Long> timeSupplier();

  default CommandMeta commandMeta(StateId stateId, EventId sagaSource) {
    return new CommandMeta(CommandId.of(idSupplier().get()), stateId, timeSupplier().get(), Option.some(sagaSource));
  }
}
