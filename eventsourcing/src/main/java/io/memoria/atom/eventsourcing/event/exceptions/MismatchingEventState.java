package io.memoria.atom.eventsourcing.event.exceptions;

import io.memoria.atom.eventsourcing.event.Event;
import io.memoria.atom.eventsourcing.state.State;

public class MismatchingEventState extends EventException {
  protected MismatchingEventState(String msg, Event event) {
    super(msg, event);
  }

  public static MismatchingEventState of(Event event) {
    var msg = "The event's stateId:%s doesn't belong here".formatted(event.shardKey());
    return new MismatchingEventState(msg, event);
  }

  public static MismatchingEventState of(Event event, State state) {
    var msg = "The Command's stateId:%s doesn't match stateId:%s".formatted(event.shardKey(), state.shardKey());
    return new MismatchingEventState(msg, event);
  }
}
