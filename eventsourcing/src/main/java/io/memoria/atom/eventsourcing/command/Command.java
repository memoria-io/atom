package io.memoria.atom.eventsourcing.command;

import io.memoria.atom.core.domain.Partitioned;
import io.memoria.atom.core.id.Id;

import java.io.Serializable;

public interface Command extends Partitioned, Serializable {
  CommandMeta meta();

  default @Override Id pKey() {
    return meta().pKey();
  }
}
