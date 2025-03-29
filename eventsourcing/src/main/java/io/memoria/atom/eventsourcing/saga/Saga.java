package io.memoria.atom.eventsourcing.saga;

import io.memoria.atom.core.id.Id;
import io.memoria.atom.eventsourcing.command.Command;
import io.memoria.atom.eventsourcing.command.CommandIds;
import io.memoria.atom.eventsourcing.command.CommandMeta;
import io.memoria.atom.eventsourcing.event.Event;
import io.memoria.atom.eventsourcing.state.StateId;

import java.util.Optional;
import java.util.function.Supplier;

public interface Saga {
  Supplier<Id> idSupplier();

  Supplier<Long> timeSupplier();

  Optional<Command> react(Event event);

  default CommandMeta commandMeta(StateId stateId, Event event) {
    return new CommandMeta(CommandIds.of(idSupplier().get()), stateId, timeSupplier().get(), event.meta().eventId());
  }
}
