package io.memoria.atom.eventsourcing.event.exceptions;

import io.memoria.atom.eventsourcing.state.State;
import io.memoria.atom.eventsourcing.ESException;
import io.memoria.atom.eventsourcing.event.Event;

public class MismatchingEventState extends ESException {
  protected MismatchingEventState(String msg) {
    super(msg);
  }

  public static MismatchingEventState of(Event event) {
    var msg = "The event's stateId:%s doesn't belong here".formatted(event.shardKey());
    return new MismatchingEventState(msg);
  }

  public static MismatchingEventState of(Event event, State state) {
    var msg = "The Command's stateId:%s doesn't match stateId:%s".formatted(event.shardKey(), state.shardKey());
    return new MismatchingEventState(msg);
  }
}
