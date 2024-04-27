package io.memoria.atom.eventsourcing.usecase.simple;

import io.memoria.atom.core.id.Id;
import io.memoria.atom.eventsourcing.command.Command;
import io.memoria.atom.eventsourcing.event.Event;
import io.memoria.atom.eventsourcing.saga.Saga;
import io.memoria.atom.eventsourcing.state.StateId;

import java.util.Optional;
import java.util.function.Supplier;

public record SimpleSaga(Supplier<Id> idSupplier, Supplier<Long> timeSupplier) implements Saga {
  @Override
  public Optional<Command> react(Event event) {
    return Optional.of(new ChangeState(commandMeta(StateId.of(0), event)));
  }
}
