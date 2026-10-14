package com.marmoush.atom.eventsourcing.usecase.simple;

import com.marmoush.atom.core.id.Id;
import com.marmoush.atom.eventsourcing.command.Command;
import com.marmoush.atom.eventsourcing.event.Event;
import com.marmoush.atom.eventsourcing.saga.Saga;
import com.marmoush.atom.eventsourcing.state.StateIds;

import java.util.Optional;
import java.util.function.Supplier;

public record SimpleSaga(Supplier<Id> idSupplier, Supplier<Long> timeSupplier) implements Saga {
  @Override
  public Optional<Command> react(Event event) {
    return Optional.of(new ChangeState(commandMeta(StateIds.of(0), event)));
  }
}
