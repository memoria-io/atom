package io.memoria.atom.eventsourcing.event.exceptions;

import io.memoria.atom.eventsourcing.event.Event;
import io.memoria.atom.eventsourcing.state.State;

@SuppressWarnings("java:S110")
public class InvalidEvolutionEvent extends RuntimeException implements EventException {
  private final Event event;

  protected InvalidEvolutionEvent(String msg, Event event) {
    super(msg);
    this.event = event;
  }

  @Override
  public String message() {
    return super.getMessage();
  }

  @Override
  public Event event() {
    return event;
  }

  public static InvalidEvolutionEvent of(State state, Event event) {
    var msg = "Invalid evolution event: %s[%s] to the state: %s[%s]".formatted(event.getClass().getSimpleName(),
                                                                               event.meta(),
                                                                               state.getClass().getSimpleName(),
                                                                               state.meta());
    return new InvalidEvolutionEvent(msg, event);
  }
}
