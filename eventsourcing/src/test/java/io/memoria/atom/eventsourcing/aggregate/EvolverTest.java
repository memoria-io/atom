package io.memoria.atom.eventsourcing.aggregate;

import io.memoria.atom.eventsourcing.command.CommandIds;
import io.memoria.atom.eventsourcing.event.EventIds;
import io.memoria.atom.eventsourcing.event.EventMeta;
import io.memoria.atom.eventsourcing.event.exceptions.InvalidEvent;
import io.memoria.atom.eventsourcing.state.StateIds;
import io.memoria.atom.eventsourcing.state.StateMeta;
import io.memoria.atom.eventsourcing.usecase.simple.SimpleEvolver;
import io.memoria.atom.eventsourcing.usecase.simple.SimpleState;
import io.memoria.atom.eventsourcing.usecase.simple.StateChanged;
import io.memoria.atom.eventsourcing.usecase.simple.StateCreated;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class EvolverTest {
  private final Evolver evolver = new SimpleEvolver();

  @Test
  void evolveCreation() {
    // Given
    var stateCreated = new StateCreated(new EventMeta(EventIds.of(0), 0, StateIds.of(0), CommandIds.of(0)));

    // When
    var event = evolver.evolve(stateCreated);

    // Then
    assertThat(event).isInstanceOf(SimpleState.class);
    assertThat(event.version()).isZero();
  }

  @Test
  void evolveEvolution() {
    // Given
    var someState = new SimpleState(new StateMeta(StateIds.of(0)));
    var stateChanged = new StateChanged(new EventMeta(EventIds.of(0), 1, StateIds.of(0), CommandIds.of(0)));

    // When
    var event = evolver.evolve(someState, stateChanged);

    // Then
    assertThat(event).isInstanceOf(SimpleState.class);
    assertThat(event.version()).isOne();
  }

  @Test
  void evolveEvolutionFail() {
    // Given
    var someState = new SimpleState(new StateMeta(StateIds.of(0)));
    var stateChanged = new StateChanged(new EventMeta(EventIds.of(0), 10, StateIds.of(0), CommandIds.of(0)));

    // When
    assertThatThrownBy(() -> evolver.evolve(someState, stateChanged)).isInstanceOf(InvalidEvent.class);
  }
}
