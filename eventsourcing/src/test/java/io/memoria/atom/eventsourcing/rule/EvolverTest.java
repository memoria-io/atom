package io.memoria.atom.eventsourcing.rule;

import io.memoria.atom.eventsourcing.command.CommandId;
import io.memoria.atom.eventsourcing.event.Event;
import io.memoria.atom.eventsourcing.event.EventId;
import io.memoria.atom.eventsourcing.event.EventMeta;
import io.memoria.atom.eventsourcing.event.exceptions.InvalidEvolutionEvent;
import io.memoria.atom.eventsourcing.event.exceptions.UnknownEvent;
import io.memoria.atom.eventsourcing.state.State;
import io.memoria.atom.eventsourcing.state.StateId;
import io.memoria.atom.eventsourcing.state.StateMeta;
import io.memoria.atom.eventsourcing.state.exceptions.UnknownState;
import org.junit.jupiter.api.Test;

import java.util.function.Function;

import static org.assertj.core.api.Assertions.assertThat;

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

  private record SomeEvolver() implements Evolver {

    @Override
    public Function<StateMeta, State> createBy(Event e) {
      return stateMeta -> {
        if (e instanceof StateCreated stateCreated) {
          return new SomeState(stateMeta);
        } else {
          throw UnknownEvent.of(e);
        }
      };
    }

    @Override
    public Function<StateMeta, State> evolve(State state, Event event) {
      return stateMeta -> {
        if (state instanceof SomeState someState) {
          return switch (event) {
            case StateCreated stateCreated -> throw InvalidEvolutionEvent.of(someState, stateCreated);
            case StateChanged _ -> new SomeState(stateMeta);
            default -> throw UnknownState.of(someState);
          };
        } else {
          throw UnknownState.of(state);
        }
      };
    }
  }
}
