package io.memoria.atom.eventsourcing.event.exceptions;

import io.memoria.atom.eventsourcing.event.Event;

public class EventRTE extends RuntimeException {
  private final Event event;

  protected EventRTE(String msg, Event event) {
    super(msg);
    this.event = event;
  }

  public Event getEvent() {
    return event;
  }
}
