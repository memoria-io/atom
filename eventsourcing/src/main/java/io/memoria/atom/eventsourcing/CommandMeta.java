package io.memoria.atom.eventsourcing;

import io.memoria.atom.core.Shardable;
import io.memoria.atom.core.id.Id;
import io.vavr.control.Option;

import java.io.Serializable;

public record CommandMeta(CommandId commandId, StateId stateId, long timestamp, Option<EventId> sagaSource)
        implements Shardable, Serializable {
  public CommandMeta {
    if (sagaSource == null) {
      throw new IllegalArgumentException("sagaSource can't be null");
    }
  }

  public CommandMeta(StateId stateId) {
    this(CommandId.of(), stateId);
  }

  public CommandMeta(CommandId commandId, StateId stateId) {
    this(commandId, stateId, System.currentTimeMillis());
  }

  public CommandMeta(CommandId commandId, StateId stateId, long timestamp) {
    this(commandId, stateId, timestamp, Option.none());
  }

  @Override
  public Id shardKey() {
    return stateId.id();
  }
}

