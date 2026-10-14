package com.marmoush.javakit.eventsourcing.event.exceptions;

import com.marmoush.javakit.eventsourcing.event.Event;
import com.marmoush.javakit.eventsourcing.state.StateId;

public class MismatchingEvent extends EventRTE {
  protected MismatchingEvent(String msg, Event event) {
    super(msg, event);
  }

  public static MismatchingEvent of(Event event, long expectedVersion) {
    var msg = "The event:%s version doesn't match expected version:%d".formatted(event.pKey(), expectedVersion);
    return new MismatchingEvent(msg, event);
  }

  public static MismatchingEvent of(StateId stateId, Event event) {
    var msg = "The event's stateId:%s doesn't match expected stateId:%s".formatted(event.pKey(), stateId);
    return new MismatchingEvent(msg, event);
  }
}
