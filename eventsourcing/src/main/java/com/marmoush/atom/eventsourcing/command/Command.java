package com.marmoush.atom.eventsourcing.command;

import com.marmoush.atom.core.domain.Partitioned;
import com.marmoush.atom.core.id.Id;
import com.marmoush.atom.eventsourcing.event.EventId;
import com.marmoush.atom.eventsourcing.state.StateId;

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
