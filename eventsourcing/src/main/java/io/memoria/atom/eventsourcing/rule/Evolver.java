package io.memoria.atom.eventsourcing.rule;

import io.memoria.atom.eventsourcing.event.Event;
import io.memoria.atom.eventsourcing.state.State;
import io.memoria.atom.eventsourcing.state.StateMeta;
import io.vavr.Function2;

public interface Evolver extends Function2<State, Event, State> {
  State apply(Event e);

  default StateMeta stateMeta(State s) {
    return s.meta().incrementVersion();
  }
}
