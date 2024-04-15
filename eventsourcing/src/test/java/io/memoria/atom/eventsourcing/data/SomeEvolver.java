package io.memoria.atom.eventsourcing.data;

import io.memoria.atom.eventsourcing.aggregate.Evolver;
import io.memoria.atom.eventsourcing.event.Event;
import io.memoria.atom.eventsourcing.event.exceptions.InvalidEvent;
import io.memoria.atom.eventsourcing.event.exceptions.UnknownEvent;
import io.memoria.atom.eventsourcing.state.State;
import io.memoria.atom.eventsourcing.state.StateMeta;
import io.memoria.atom.eventsourcing.state.exceptions.UnknownState;

public record SomeEvolver() implements Evolver {
  @Override
  public State evolve(Event event, StateMeta stateMeta) {
    if (event instanceof StateCreated) {
      return new SomeState(stateMeta);
    } else {
      throw UnknownEvent.of(event);
    }
  }

  @Override
  public State evolve(State state, Event event, StateMeta stateMeta) {
    if (state instanceof SomeState someState) {
      return switch (event) {
        case StateCreated stateCreated -> throw InvalidEvent.ofEvolution(someState, stateCreated);
        case StateChanged _ -> new SomeState(stateMeta);
        default -> throw UnknownState.of(someState);
      };
    } else {
      throw UnknownState.of(state);
    }
  }
}