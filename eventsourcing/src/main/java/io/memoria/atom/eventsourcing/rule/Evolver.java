package io.memoria.atom.eventsourcing.rule;

import io.memoria.atom.eventsourcing.event.Event;
import io.memoria.atom.eventsourcing.event.exceptions.InvalidEvolutionEvent;
import io.memoria.atom.eventsourcing.state.State;
import io.memoria.atom.eventsourcing.state.StateMeta;

import java.util.function.Function;

public interface Evolver {

  Function<StateMeta, State> createBy(Event event);

  Function<StateMeta, State> evolve(State state, Event event);

  default State apply(Event e) {
    return createBy(e).apply(stateMeta(e));
  }

  default State apply(State state, Event event) {
    return evolve(state, event).apply(stateMeta(state, event));
  }

  default StateMeta stateMeta(Event e) {
    return new StateMeta(e.meta().stateId());
  }

  default StateMeta stateMeta(State s, Event e) {
    if (s.version() + 1 != e.version()) {
      throw InvalidEvolutionEvent.of(s, e);
    }
    return s.meta().incrementVersion();
  }
}
