package io.memoria.atom.eventsourcing.aggregate;

import io.memoria.atom.eventsourcing.event.Event;
import io.memoria.atom.eventsourcing.event.exceptions.InvalidEvent;
import io.memoria.atom.eventsourcing.state.State;
import io.memoria.atom.eventsourcing.state.StateMeta;

public interface Evolver {

  /**
   * @param event     A creational event
   * @param stateMeta the new State stateMeta
   * @return a new State with stateMeta as its meta value
   */
  State evolve(Event event, StateMeta stateMeta);

  /**
   * @param state     initial State
   * @param event     evolution event to be applied upon the initial state
   * @param stateMeta of
   * @return state with stateMeta as its meta value
   */
  State evolve(State state, Event event, StateMeta stateMeta);

  default State evolve(Event e) {
    return evolve(e, stateMeta(e));
  }

  default State evolve(State state, Event event) {
    return evolve(state, event, stateMeta(state, event));
  }

  default StateMeta stateMeta(Event e) {
    return new StateMeta(e.meta().stateId());
  }

  default StateMeta stateMeta(State s, Event e) {
    if (s.version() + 1 != e.version()) {
      throw InvalidEvent.ofEvolution(s, e);
    }
    return s.meta().incrementVersion();
  }
}
