package io.memoria.atom.eventsourcing;

import io.memoria.atom.core.domain.Shardable;
import io.memoria.atom.core.id.Id;

import java.io.Serializable;

public interface Command extends Shardable, Serializable {
  CommandMeta meta();

  default @Override Id shardKey() {
    return meta().shardKey();
  }
}
