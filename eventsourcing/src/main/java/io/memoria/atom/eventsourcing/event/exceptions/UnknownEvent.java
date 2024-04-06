package io.memoria.atom.eventsourcing.event.exceptions;

import io.memoria.atom.eventsourcing.event.Event;

public class UnknownEvent extends RuntimeException implements EventException {
  private static final String MESSAGE = "Unknown Event: %s[%s] implementation";
  private final Event event;

  protected UnknownEvent(Event event) {
    super(MESSAGE.formatted(event.getClass().getSimpleName(), event.meta()));
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

  public static UnknownEvent of(Event event) {
    return new UnknownEvent(event);
  }
}
