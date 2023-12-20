package io.memoria.atom.eventsourcing;

import io.memoria.atom.core.Shardable;
import io.memoria.atom.core.Versioned;
import io.memoria.atom.core.id.Id;

import java.io.Serializable;

public record StateMeta(StateId stateId, long version) implements Shardable, Versioned, Serializable {
  public StateMeta {
    if (version < 0) {
      throw new IllegalArgumentException("Version can't be less than zero!");
    }
  }

  public StateMeta() {
    this(StateId.of());
  }

  public StateMeta(StateId stateId) {
    this(stateId, 0);
  }

  @Override
  public Id shardKey() {
    return stateId.id();
  }

  public StateMeta incrementVersion() {
    return new StateMeta(stateId, version + 1);
  }
}
