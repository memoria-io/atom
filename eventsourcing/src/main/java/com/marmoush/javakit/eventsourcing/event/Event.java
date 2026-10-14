package com.marmoush.javakit.eventsourcing.event;

import com.marmoush.javakit.core.domain.Partitioned;
import com.marmoush.javakit.core.domain.Versioned;
import com.marmoush.javakit.core.id.Id;
import com.marmoush.javakit.eventsourcing.command.CommandId;
import com.marmoush.javakit.eventsourcing.state.StateId;

import java.io.Serializable;
import java.util.Optional;

public interface Event extends Partitioned, Versioned, Serializable {
  EventMeta meta();

  default EventId eventId() {
    return meta().eventId();
  }

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

  default @Override long version() {
    return meta().version();
  }
}
