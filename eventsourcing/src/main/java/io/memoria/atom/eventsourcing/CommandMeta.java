package io.memoria.atom.eventsourcing;

import io.vavr.control.Option;

import java.io.Serializable;

public record CommandMeta(CommandId commandId, StateId stateId, long timestamp, Option<EventId> sagaSource)
        implements Shardable, Serializable {
  public CommandMeta {
    if (sagaSource == null) {
      throw new IllegalArgumentException("sagaSource can not be null");
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

  public CommandMeta(CommandId commandId, StateId stateId, long timestamp, EventId sagaSource) {
    this(commandId, stateId, timestamp, Option.of(sagaSource));
  }
}

