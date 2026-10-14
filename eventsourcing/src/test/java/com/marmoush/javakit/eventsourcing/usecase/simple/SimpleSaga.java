package com.marmoush.javakit.eventsourcing.usecase.simple;

import com.marmoush.javakit.core.id.Id;
import com.marmoush.javakit.eventsourcing.command.Command;
import com.marmoush.javakit.eventsourcing.event.Event;
import com.marmoush.javakit.eventsourcing.saga.Saga;
import com.marmoush.javakit.eventsourcing.state.StateIds;

import java.util.Optional;
import java.util.function.Supplier;

public record SimpleSaga(Supplier<Id> idSupplier, Supplier<Long> timeSupplier) implements Saga {
  @Override
  public Optional<Command> react(Event event) {
    return Optional.of(new ChangeState(commandMeta(StateIds.of(0), event)));
  }
}
