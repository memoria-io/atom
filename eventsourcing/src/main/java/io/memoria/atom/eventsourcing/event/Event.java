package io.memoria.atom.eventsourcing.event;

import io.memoria.atom.core.domain.Partitioned;
import io.memoria.atom.core.domain.Versioned;
import io.memoria.atom.core.id.Id;

import java.io.Serializable;

public interface Event extends Partitioned, Versioned, Serializable {
  EventMeta meta();

  default @Override Id pKey() {
    return meta().pKey();
  }

  default @Override long version() {
    return meta().version();
  }
}
