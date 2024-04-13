package io.memoria.atom.eventsourcing.command;

import io.memoria.atom.core.domain.Partitioned;
import io.memoria.atom.core.id.Id;
import io.memoria.atom.eventsourcing.event.EventId;
import io.memoria.atom.eventsourcing.state.StateId;

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
