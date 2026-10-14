package com.marmoush.atom.eventsourcing.state;

import com.marmoush.atom.core.domain.Partitioned;
import com.marmoush.atom.core.domain.Versioned;
import com.marmoush.atom.core.id.Id;

import java.io.Serializable;

public interface State extends Partitioned, Versioned, Serializable {
  StateMeta meta();

  State withMeta(StateMeta meta);

  default StateId stateId() {
    return meta().stateId();
  }

  default @Override Id pKey() {
    return meta().pKey();
  }

  default @Override long version() {
    return meta().version();
  }
}
