package com.marmoush.javakit.eventsourcing.command;

import com.marmoush.javakit.core.domain.Partitioned;
import com.marmoush.javakit.core.id.Id;
import com.marmoush.javakit.eventsourcing.event.EventId;
import com.marmoush.javakit.eventsourcing.state.StateId;

import java.io.Serializable;
import java.util.Optional;

public interface Command extends Partitioned, Serializable {
  CommandMeta meta();

  default CommandId commandId() {
    return meta().commandId();
  }

  default StateId stateId() {
    return meta().stateId();
  }

  default Optional<EventId> sagaSource() {
    return meta().sagaSource();
  }

  default @Override Id pKey() {
    return meta().pKey();
  }
}
