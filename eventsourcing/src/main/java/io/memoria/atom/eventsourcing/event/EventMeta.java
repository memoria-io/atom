package io.memoria.atom.eventsourcing.event;

import io.memoria.atom.core.domain.Shardable;
import io.memoria.atom.core.domain.Versioned;
import io.memoria.atom.core.id.Id;
import io.memoria.atom.eventsourcing.state.StateId;
import io.memoria.atom.eventsourcing.command.CommandId;


import java.io.Serializable;
import java.util.Optional;

public record EventMeta(EventId eventId,
                        long version,
                        StateId stateId,
                        CommandId commandId,
                        long timestamp,
                        Optional<EventId> sagaSource) implements Shardable, Versioned, Serializable {
  public EventMeta {
    if (version < 0) {
      throw new IllegalArgumentException("version can't be less than zero");
    }
    if (sagaSource.isEmpty()) {
      throw new IllegalArgumentException("Saga source can't be null");
    }
  }

  public EventMeta(EventId id, long version, StateId stateId, CommandId commandId) {
    this(id, version, stateId, commandId, System.currentTimeMillis());
  }

  public EventMeta(EventId id, long version, StateId stateId, CommandId commandId, long timestamp) {
    this(id, version, stateId, commandId, timestamp, Optional.empty());
  }

  @Override
  public Id shardKey() {
    return stateId;
  }
}

