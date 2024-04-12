package io.memoria.atom.eventsourcing.state;

import io.memoria.atom.core.domain.Partitioned;
import io.memoria.atom.core.domain.Versioned;
import io.memoria.atom.core.id.Id;

import java.io.Serializable;

public interface State extends Partitioned, Versioned, Serializable {
  StateMeta meta();

  default @Override Id pKey() {
    return meta().pKey();
  }

  default @Override long version() {
    return meta().version();
  }
}
