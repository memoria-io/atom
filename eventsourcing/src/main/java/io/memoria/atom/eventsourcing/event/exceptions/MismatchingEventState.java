package io.memoria.atom.eventsourcing.event.exceptions;

import io.memoria.atom.eventsourcing.event.Event;
import io.memoria.atom.eventsourcing.state.StateId;

@SuppressWarnings("java:S110")
public class MismatchingEventState extends EventRuntimeException {
  protected MismatchingEventState(String msg, Event event) {
    super(msg, event);
  }

  public static MismatchingEventState of(Event event) {
    var msg = "The event's stateId:%s doesn't belong here".formatted(event.shardKey());
    return new MismatchingEventState(msg, event);
  }

  public static MismatchingEventState of(Event event, StateId stateId) {
    var msg = "The event's stateId:%s doesn't match expected stateId:%s".formatted(event.shardKey(), stateId);
    return new MismatchingEventState(msg, event);
  }
}
