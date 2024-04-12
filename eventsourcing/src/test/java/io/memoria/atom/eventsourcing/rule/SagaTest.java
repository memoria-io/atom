package io.memoria.atom.eventsourcing.rule;

import io.memoria.atom.core.id.Id;
import io.memoria.atom.eventsourcing.command.Command;
import io.memoria.atom.eventsourcing.command.CommandId;
import io.memoria.atom.eventsourcing.event.Event;
import io.memoria.atom.eventsourcing.event.EventId;
import io.memoria.atom.eventsourcing.event.EventMeta;
import io.memoria.atom.eventsourcing.state.StateId;
import org.junit.jupiter.api.Test;

import java.util.Optional;
import java.util.function.Supplier;

import static org.assertj.core.api.Assertions.assertThat;

class SagaTest {
  private final Saga saga = new SomeSaga(() -> Id.of(0), () -> 0L);

  @Test
  @SuppressWarnings("OptionalGetWithoutIsPresent")
  void apply() {
    // Given
    var eventMeta = new EventMeta(EventId.of(0), 0, StateId.of(0), CommandId.of(0));
    var stateCreated = new StateCreated(eventMeta);

    // When
    var command = saga.apply(stateCreated).get();

    // Then
    assertThat(command).isInstanceOf(ChangeState.class);
    assertThat(command.meta().sagaSource()).contains(stateCreated.meta().eventId());
  }

  private record SomeSaga(Supplier<Id> idSupplier, Supplier<Long> timeSupplier) implements Saga {
    @Override
    public Optional<Command> apply(Event event) {
      return Optional.of(new ChangeState(commandMeta(StateId.of(0), event)));
    }
  }
}


