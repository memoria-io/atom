package io.memoria.atom.core.eventsourcing;

import java.io.Serializable;

public interface Command extends Shardable, Serializable {
  CommandId commandId();

  long timestamp();
}
