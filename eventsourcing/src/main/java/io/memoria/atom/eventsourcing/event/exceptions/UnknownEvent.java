package io.memoria.atom.eventsourcing.event.exceptions;

import io.memoria.atom.eventsourcing.event.Event;

@SuppressWarnings("java:S110")
public class UnknownEvent extends EventRuntimeException {
  private static final String MESSAGE = "Unknown Event: %s[%s] implementation";

  protected UnknownEvent(Event event) {
    super(MESSAGE.formatted(event.getClass().getSimpleName(), event.meta()), event);
  }

  public static UnknownEvent of(Event event) {
    return new UnknownEvent(event);
  }
}
