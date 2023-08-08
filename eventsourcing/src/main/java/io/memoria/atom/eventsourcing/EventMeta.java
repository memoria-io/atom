package io.memoria.atom.eventsourcing;

import io.vavr.control.Option;

import java.io.Serializable;

public record EventMeta(EventId eventId,
                        CommandId commandId,
                        StateId stateId,
                        long timestamp,
                        Option<EventId> sagaSource) implements Shardable, Serializable {
  public EventMeta {
    if (sagaSource == null) {
      throw new IllegalArgumentException("sagaSource can not be null");
    }
  }

  public EventMeta(EventId id, CommandId commandId, StateId stateId, long timestamp) {
    this(id, commandId, stateId, timestamp, Option.none());
  }

  public EventMeta(EventId id, CommandId commandId, StateId stateId) {
    this(id, commandId, stateId, System.currentTimeMillis(), Option.none());
  }

  public EventMeta(CommandId commandId, StateId stateId) {
    this(EventId.of(),commandId, stateId, System.currentTimeMillis(), Option.none());
  }
}

