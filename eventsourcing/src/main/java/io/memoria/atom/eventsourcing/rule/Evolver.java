package io.memoria.atom.eventsourcing.rule;

import io.memoria.atom.eventsourcing.event.Event;
import io.memoria.atom.eventsourcing.state.State;
import io.memoria.atom.eventsourcing.state.StateMeta;

import java.util.function.BiFunction;

public interface Evolver extends BiFunction<State, Event, State> {
  State apply(Event e);

  default StateMeta stateMeta(State s) {
    return s.meta().incrementVersion();
  }
}
