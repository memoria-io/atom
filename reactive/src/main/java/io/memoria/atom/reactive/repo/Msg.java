package io.memoria.atom.reactive.repo;

import io.memoria.atom.core.id.Id;
import io.memoria.atom.core.eventsourcing.Entity;
import io.memoria.atom.core.eventsourcing.Shardable;
import io.memoria.atom.core.eventsourcing.StateId;

import java.io.Serializable;

public record Msg(Id id, StateId stateId, String value, String topic, int partition)
        implements Shardable, Entity, Serializable {
  public Msg {
    if (partition < 0) {
      throw new IllegalArgumentException("partition field is set to %d which is below 0".formatted(partition));
    }
  }
}
