package com.marmoush.atom.eventsourcing.saga;

import com.marmoush.atom.core.id.Id;
import com.marmoush.atom.eventsourcing.command.Command;
import com.marmoush.atom.eventsourcing.command.CommandIds;
import com.marmoush.atom.eventsourcing.command.CommandMeta;
import com.marmoush.atom.eventsourcing.event.Event;
import com.marmoush.atom.eventsourcing.state.StateId;

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
