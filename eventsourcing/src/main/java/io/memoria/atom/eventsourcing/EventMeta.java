package io.memoria.atom.eventsourcing;

import io.vavr.control.Option;

import java.io.Serializable;

public record EventMeta(EventId eventId,
                        long seqId,
                        CommandId commandId,
                        StateId stateId,
                        long timestamp,
                        Option<EventId> sagaSource) implements Serializable {
  public EventMeta {
    if (sagaSource == null) {
      throw new IllegalArgumentException("sagaSource can not be null");
    }
  }

  public EventMeta(EventId id, long seqId, CommandId commandId, StateId stateId, long timestamp) {
    this(id, seqId, commandId, stateId, timestamp, Option.none());
  }

  public EventMeta(EventId id, long seqId, CommandId commandId, StateId stateId) {
    this(id, seqId, commandId, stateId, System.currentTimeMillis(), Option.none());
  }

  public EventMeta(long seqId, CommandId commandId, StateId stateId) {
    this(EventId.of(), seqId, commandId, stateId, System.currentTimeMillis(), Option.none());
  }
}

