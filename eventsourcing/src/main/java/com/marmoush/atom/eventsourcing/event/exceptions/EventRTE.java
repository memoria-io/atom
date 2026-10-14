package com.marmoush.atom.eventsourcing.event.exceptions;

import com.marmoush.atom.eventsourcing.event.Event;

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
