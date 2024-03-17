package io.memoria.atom.eventsourcing.event.exceptions;

import io.memoria.atom.eventsourcing.ESRuntimeException;
import io.memoria.atom.eventsourcing.event.Event;

public class EventRuntimeException extends ESRuntimeException {
  private final Event event;

  protected EventRuntimeException(String msg, Event event) {
    super(msg);
    this.event = event;
  }

  public Event getEvent() {
    return event;
  }
}
