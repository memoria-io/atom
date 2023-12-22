package io.memoria.atom.eventsourcing;

import io.memoria.atom.core.Shardable;
import io.memoria.atom.core.Versioned;
import io.memoria.atom.core.id.Id;

import java.io.Serializable;

public interface State extends Shardable, Versioned, Serializable {
  StateMeta meta();

  default @Override Id shardKey() {
    return meta().shardKey();
  }

  default @Override long version() {
    return meta().version();
  }
}
