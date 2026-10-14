package com.marmoush.atom.eventsourcing.saga;

import com.marmoush.atom.core.id.Ids;
import com.marmoush.atom.eventsourcing.command.CommandIds;
import com.marmoush.atom.eventsourcing.event.EventIds;
import com.marmoush.atom.eventsourcing.event.EventMeta;
import com.marmoush.atom.eventsourcing.state.StateIds;
import com.marmoush.atom.eventsourcing.usecase.simple.ChangeState;
import com.marmoush.atom.eventsourcing.usecase.simple.SimpleSaga;
import com.marmoush.atom.eventsourcing.usecase.simple.StateCreated;
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


