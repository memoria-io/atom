package io.memoria.atom.eventsourcing;

import java.io.Serializable;

public record EventMeta(EventId eventId, long version, StateId stateId, CommandId commandId, long timestamp)
        implements Shardable, Versioned, Serializable {
  public EventMeta {
    if (version < 0) {
      throw new IllegalArgumentException("version can't be less than zero");
    }
  }

  public EventMeta(CommandId commandId, long version, StateId stateId) {
    this(EventId.of(), version, stateId, commandId);
  }

  public EventMeta(EventId id, long version, StateId stateId, CommandId commandId) {
    this(id, version, stateId, commandId, System.currentTimeMillis());
  }
}

