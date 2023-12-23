package io.memoria.atom.eventsourcing;

import io.memoria.atom.core.Shardable;
import io.memoria.atom.core.Versioned;
import io.memoria.atom.core.id.Id;
import io.vavr.control.Option;

import java.io.Serializable;

public record EventMeta(EventId eventId,
                        long version,
                        StateId stateId,
                        CommandId commandId,
                        long timestamp,
                        Option<EventId> sagaSource) implements Shardable, Versioned, Serializable {
  public EventMeta {
    if (version < 0) {
      throw new IllegalArgumentException("version can't be less than zero");
    }
    if (sagaSource == null) {
      throw new IllegalArgumentException("Saga source can't be null");
    }
  }

  public EventMeta(EventId id, CommandId commandId, long version, StateId stateId) {
    this(id, version, stateId, commandId);
  }

  public EventMeta(EventId id, long version, StateId stateId, CommandId commandId) {
    this(id, version, stateId, commandId, System.currentTimeMillis());
  }

  public EventMeta(EventId id, long version, StateId stateId, CommandId commandId, long timestamp) {
    this(id, version, stateId, commandId, timestamp, Option.none());
  }

  @Override
  public Id shardKey() {
    return stateId;
  }
}

