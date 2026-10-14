package com.marmoush.javakit.eventsourcing.state;

import com.marmoush.javakit.core.domain.Partitioned;
import com.marmoush.javakit.core.domain.Versioned;
import com.marmoush.javakit.core.id.Id;

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
