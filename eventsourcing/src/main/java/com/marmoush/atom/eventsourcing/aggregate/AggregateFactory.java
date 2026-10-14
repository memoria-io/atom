package com.marmoush.atom.eventsourcing.aggregate;

import com.marmoush.atom.eventsourcing.state.StateId;

public interface AggregateFactory {
  Aggregate create(StateId stateId);
}
