package io.memoria.atom.eventsourcing.command;

import io.memoria.atom.core.domain.Shardable;
import io.memoria.atom.core.id.Id;
import io.memoria.atom.eventsourcing.event.EventId;
import io.memoria.atom.eventsourcing.state.StateId;


import java.io.Serializable;
import java.util.Optional;

public record CommandMeta(CommandId commandId, StateId stateId, long timestamp, Optional<EventId> sagaSource)
        implements Shardable, Serializable {
  public CommandMeta {
    if (sagaSource == null) {
      throw new IllegalArgumentException("sagaSource can't be null");
    }
  }

  public CommandMeta(CommandId commandId, StateId stateId) {
    this(commandId, stateId, System.currentTimeMillis());
  }

  public CommandMeta(CommandId commandId, StateId stateId, long timestamp) {
    this(commandId, stateId, timestamp, Optional.empty());
  }

  @Override
  public Id shardKey() {
    return stateId;
  }
}

