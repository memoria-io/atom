package io.memoria.atom.eventsourcing.rule;

import io.memoria.atom.core.id.Id;
import io.memoria.atom.eventsourcing.command.Command;
import io.memoria.atom.eventsourcing.command.CommandId;
import io.memoria.atom.eventsourcing.command.CommandMeta;
import io.memoria.atom.eventsourcing.event.Event;
import io.memoria.atom.eventsourcing.event.EventId;
import io.memoria.atom.eventsourcing.state.StateId;

import java.util.Optional;
import java.util.function.Function;
import java.util.function.Supplier;

public interface Saga extends Function<Event, Optional<Command>> {
  Supplier<Id> idSupplier();

  Supplier<Long> timeSupplier();

  default CommandMeta commandMeta(StateId stateId, EventId sagaSource) {
    return new CommandMeta(CommandId.of(idSupplier().get()), stateId, timeSupplier().get(), Optional.of(sagaSource));
  }
}
