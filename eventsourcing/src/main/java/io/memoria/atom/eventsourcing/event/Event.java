package io.memoria.atom.eventsourcing.event;

import io.memoria.atom.core.domain.Shardable;
import io.memoria.atom.core.domain.Versioned;
import io.memoria.atom.core.id.Id;

import java.io.Serializable;

public interface Event extends Shardable, Versioned, Serializable {
  EventMeta meta();

  default @Override Id shardKey() {
    return meta().shardKey();
  }

  default @Override long version() {
    return meta().version();
  }
}
