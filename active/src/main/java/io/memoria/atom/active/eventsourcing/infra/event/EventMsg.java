package io.memoria.atom.active.eventsourcing.infra.event;

import io.memoria.atom.core.eventsourcing.StateId;

public interface EventMsg {
  String topic();

  StateId stateId();

  int seqId();

  String value();

  static EventMsg create(String topic, StateId stateId, int seqId, String value) {
    return new DefaultEventMsg(topic, stateId, seqId, value);
  }
}
