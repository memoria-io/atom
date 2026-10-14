package com.marmoush.atom.eventsourcing.event.exceptions;

import com.marmoush.atom.eventsourcing.event.Event;
import com.marmoush.atom.eventsourcing.state.State;

public class InvalidEvent extends EventRTE {
  protected InvalidEvent(String msg, Event event) {
    super(msg, event);
  }

  public static InvalidEvent ofCreation(Event event) {
    var msg = "Invalid creation event: %s[%s]".formatted(event.getClass().getSimpleName(), event.meta());
    return new InvalidEvent(msg, event);
  }

  public static InvalidEvent ofEvolution(State state, Event event) {
    var msg = "Invalid evolution event: %s[%s] to the state: %s[%s]".formatted(event.getClass().getSimpleName(),
                                                                               event.meta(),
                                                                               state.getClass().getSimpleName(),
                                                                               state.meta());
    return new InvalidEvent(msg, event);
  }
}
