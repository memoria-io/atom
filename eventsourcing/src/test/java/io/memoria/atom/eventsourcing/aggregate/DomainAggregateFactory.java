package io.memoria.atom.eventsourcing.aggregate;

import io.memoria.atom.core.id.Ids;
import io.memoria.atom.eventsourcing.event.repo.EventRepo;
import io.memoria.atom.eventsourcing.state.StateId;
import io.memoria.atom.eventsourcing.usecase.simple.SimpleDecider;
import io.memoria.atom.eventsourcing.usecase.simple.SimpleEvolver;

import java.util.UUID;

public class DomainAggregateFactory implements AggregateFactory {
  private final Decider decider = new SimpleDecider(() -> Ids.of(UUID.randomUUID()), () -> 0L);
  private final Evolver evolver = new SimpleEvolver();
  private final EventRepo eventRepo = EventRepo.inMemory();

  @Override
  public Aggregate create(StateId stateId) {
    return Aggregate.create(stateId, decider, evolver, eventRepo);
  }
}
