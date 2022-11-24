package io.memoria.atom.active.eventsourcing.repo;

import io.memoria.atom.core.eventsourcing.StateId;

record DefaultEventMsg(String topic, StateId stateId, int seqId, String value) implements EventMsg {}
