package io.memoria.atom.eventsourcing.saga;

import io.memoria.atom.core.id.Ids;
import io.memoria.atom.eventsourcing.command.CommandIds;
import io.memoria.atom.eventsourcing.event.EventIds;
import io.memoria.atom.eventsourcing.event.EventMeta;
import io.memoria.atom.eventsourcing.state.StateIds;
import io.memoria.atom.eventsourcing.usecase.simple.ChangeState;
import io.memoria.atom.eventsourcing.usecase.simple.SimpleSaga;
import io.memoria.atom.eventsourcing.usecase.simple.StateCreated;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class SagaTest {
  private final Saga saga = new SimpleSaga(() -> Ids.of(0), () -> 0L);

  @Test
  @SuppressWarnings("OptionalGetWithoutIsPresent")
  void react() {
    // Given
    var eventMeta = new EventMeta(EventIds.of(0), 0, StateIds.of(0), CommandIds.of(0));
    var stateCreated = new StateCreated(eventMeta);

    // When
    var command = saga.react(stateCreated).get();

    // Then
    assertThat(command).isInstanceOf(ChangeState.class);
    assertThat(command.meta().sagaSource()).contains(stateCreated.meta().eventId());
  }

}


