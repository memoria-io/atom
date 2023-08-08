package io.memoria.atom.eventsourcing;

import java.io.Serializable;

public record CommandMeta(CommandId commandId, StateId stateId, long timestamp) implements Shardable, Serializable {
  public CommandMeta(StateId stateId) {
    this(CommandId.of(), stateId);
  }

  public CommandMeta(CommandId commandId, StateId stateId) {
    this(commandId, stateId, System.currentTimeMillis());
  }
}

