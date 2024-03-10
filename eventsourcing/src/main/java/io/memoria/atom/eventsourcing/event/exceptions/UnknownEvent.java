package io.memoria.atom.eventsourcing.event.exceptions;

import io.memoria.atom.eventsourcing.event.Event;

public class UnknownEvent extends EventException {
  private static final String message = "Unknown Event: %s[%s] implementation";

  protected UnknownEvent(Event event) {
    super(message.formatted(event.getClass().getSimpleName(), event.meta()), event);
  }

  public static UnknownEvent of(Event event) {
    return new UnknownEvent(event);
  }
}
