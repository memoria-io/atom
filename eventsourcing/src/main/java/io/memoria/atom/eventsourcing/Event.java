package io.memoria.atom.eventsourcing;

import java.io.Serializable;

public interface Event extends Shardable, Serializable {
  EventMeta meta();

  default EventId id() {
    return meta().eventId();
  }

  default CommandId commandId() {
    return meta().commandId();
  }

  default long seqId() {
    return meta().seqId();
  }

  @Override
  default StateId stateId() {
    return meta().stateId();
  }
}
