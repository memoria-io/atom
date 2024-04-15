package io.memoria.atom.eventsourcing.aggregate;

import io.memoria.atom.eventsourcing.command.CommandId;
import io.memoria.atom.eventsourcing.data.SomeEvolver;
import io.memoria.atom.eventsourcing.data.SomeState;
import io.memoria.atom.eventsourcing.data.StateChanged;
import io.memoria.atom.eventsourcing.data.StateCreated;
import io.memoria.atom.eventsourcing.event.EventId;
import io.memoria.atom.eventsourcing.event.EventMeta;
import io.memoria.atom.eventsourcing.event.exceptions.InvalidEvolutionEvent;
import io.memoria.atom.eventsourcing.state.StateId;
import io.memoria.atom.eventsourcing.state.StateMeta;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

public class EvolverTest {
  private final Evolver evolver = new SomeEvolver();

  @Test
  void applyCreation() {
    // Given
    var stateCreated = new StateCreated(new EventMeta(EventId.of(0), 0, StateId.of(0), CommandId.of(0)));

    // When
    var event = evolver.apply(stateCreated);

    // Then
    assertThat(event).isInstanceOf(SomeState.class);
    assertThat(event.version()).isEqualTo(0);
  }

  @Test
  void applyEvolution() {
    // Given
    var someState = new SomeState(new StateMeta(StateId.of(0)));
    var stateChanged = new StateChanged(new EventMeta(EventId.of(0), 1, StateId.of(0), CommandId.of(0)));

    // When
    var event = evolver.apply(someState, stateChanged);

    // Then
    assertThat(event).isInstanceOf(SomeState.class);
    assertThat(event.version()).isEqualTo(1);
  }

  @Test
  void applyEvolutionFail() {
    // Given
    var someState = new SomeState(new StateMeta(StateId.of(0)));
    var stateChanged = new StateChanged(new EventMeta(EventId.of(0), 10, StateId.of(0), CommandId.of(0)));

    // When
    assertThatThrownBy(() -> evolver.apply(someState, stateChanged)).isInstanceOf(InvalidEvolutionEvent.class);
  }
}
