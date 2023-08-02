package io.memoria.atom.eventsourcing;

import io.vavr.control.Option;

import java.io.Serializable;

public interface Command extends Shardable, Serializable {
  default Option<EventId> sagaEventId() {
    return Option.none();
  }

  CommandId commandId();

  long timestamp();
}
