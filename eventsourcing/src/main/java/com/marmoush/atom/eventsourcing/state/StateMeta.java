package com.marmoush.atom.eventsourcing.state;

import com.marmoush.atom.core.domain.Partitioned;
import com.marmoush.atom.core.domain.Versioned;
import com.marmoush.atom.core.id.Id;

import java.io.Serializable;

public record StateMeta(StateId stateId, long version) implements Partitioned, Versioned, Serializable {
  public StateMeta {
    if (version < 0) {
      throw new IllegalArgumentException("Version can't be less than zero!");
    }
  }

  public StateMeta(StateId stateId) {
    this(stateId, 0);
  }

  @Override
  public Id pKey() {
    return stateId;
  }

  public StateMeta incrementVersion() {
    return new StateMeta(stateId, version + 1);
  }
}
