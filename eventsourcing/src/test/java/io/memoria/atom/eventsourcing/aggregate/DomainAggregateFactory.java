package io.memoria.atom.eventsourcing.aggregate;

import io.memoria.atom.core.id.Id;
import io.memoria.atom.eventsourcing.data.SomeDecider;
import io.memoria.atom.eventsourcing.data.SomeEvolver;
import io.memoria.atom.eventsourcing.state.StateId;

import java.util.UUID;

public class DomainAggregateFactory implements AggregateFactory {
  private final Decider decider = new SomeDecider(() -> Id.of(UUID.randomUUID()), () -> 0L);
  private final Evolver evolver = new SomeEvolver();

  @Override
  public DefaultAggregate create(StateId stateId) {
    return DefaultAggregate.create(decider, evolver, stateId);
  }
}
