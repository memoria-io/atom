package io.memoria.atom.eventsourcing;

import java.io.Serializable;

public interface Command extends Shardable, Serializable {
  CommandMeta meta();

  default CommandId commandId() {
    return meta().commandId();
  }

  @Override
  default StateId stateId() {
    return meta().stateId();
  }
}
