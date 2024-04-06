package io.memoria.atom.eventsourcing.event.exceptions;

import io.memoria.atom.eventsourcing.event.Event;
import io.memoria.atom.eventsourcing.state.State;

public class InvalidEvolutionEvent extends EventRuntimeException {
  protected InvalidEvolutionEvent(String msg, Event event) {
    super(msg, event);
  }

  public static InvalidEvolutionEvent of(State state, Event event) {
    var msg = "Invalid evolution event: %s[%s] to the state: %s[%s]".formatted(event.getClass().getSimpleName(),
                                                                               event.meta(),
                                                                               state.getClass().getSimpleName(),
                                                                               state.meta());
    return new InvalidEvolutionEvent(msg, event);
  }
}
