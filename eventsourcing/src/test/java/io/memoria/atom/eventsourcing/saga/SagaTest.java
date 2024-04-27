package io.memoria.atom.eventsourcing.saga;

import io.memoria.atom.core.id.Id;
import io.memoria.atom.eventsourcing.command.CommandId;
import io.memoria.atom.eventsourcing.usecase.simple.ChangeState;
import io.memoria.atom.eventsourcing.usecase.simple.SimpleSaga;
import io.memoria.atom.eventsourcing.usecase.simple.StateCreated;
import io.memoria.atom.eventsourcing.event.EventId;
import io.memoria.atom.eventsourcing.event.EventMeta;
import io.memoria.atom.eventsourcing.state.StateId;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class SagaTest {
  private final Saga saga = new SimpleSaga(() -> Id.of(0), () -> 0L);

  @Test
  @SuppressWarnings("OptionalGetWithoutIsPresent")
  void react() {
    // Given
    var eventMeta = new EventMeta(EventId.of(0), 0, StateId.of(0), CommandId.of(0));
    var stateCreated = new StateCreated(eventMeta);

    // When
    var command = saga.react(stateCreated).get();

    // Then
    assertThat(command).isInstanceOf(ChangeState.class);
    assertThat(command.meta().sagaSource()).contains(stateCreated.meta().eventId());
  }

}


