package com.marmoush.javakit.eventsourcing.saga;

import com.marmoush.javakit.core.id.Ids;
import com.marmoush.javakit.eventsourcing.command.CommandIds;
import com.marmoush.javakit.eventsourcing.event.EventIds;
import com.marmoush.javakit.eventsourcing.event.EventMeta;
import com.marmoush.javakit.eventsourcing.state.StateIds;
import com.marmoush.javakit.eventsourcing.usecase.simple.ChangeState;
import com.marmoush.javakit.eventsourcing.usecase.simple.SimpleSaga;
import com.marmoush.javakit.eventsourcing.usecase.simple.StateCreated;
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


