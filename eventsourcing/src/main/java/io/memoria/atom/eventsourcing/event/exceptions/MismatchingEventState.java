package io.memoria.atom.eventsourcing.event.exceptions;

import io.memoria.atom.eventsourcing.event.Event;
import io.memoria.atom.eventsourcing.state.StateId;

public class MismatchingEventState extends RuntimeException implements EventException {
  private final Event event;

  protected MismatchingEventState(String msg, Event event) {
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

  public static MismatchingEventState of(Event event) {
    var msg = "The event's stateId:%s doesn't belong here".formatted(event.shardKey());
    return new MismatchingEventState(msg, event);
  }

  public static MismatchingEventState of(StateId stateId, Event event) {
    var msg = "The event's stateId:%s doesn't match expected stateId:%s".formatted(event.shardKey(), stateId);
    return new MismatchingEventState(msg, event);
  }
}
