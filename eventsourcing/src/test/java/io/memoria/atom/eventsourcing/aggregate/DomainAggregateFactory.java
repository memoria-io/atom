package io.memoria.atom.eventsourcing.aggregate;

import io.memoria.atom.core.id.Id;
import io.memoria.atom.eventsourcing.data.SomeDecider;
import io.memoria.atom.eventsourcing.data.SomeEvolver;
import io.memoria.atom.eventsourcing.event.repo.EventRepo;
import io.memoria.atom.eventsourcing.state.StateId;

import java.util.UUID;

public class DomainAggregateFactory implements AggregateFactory {
  private final Decider decider = new SomeDecider(() -> Id.of(UUID.randomUUID()), () -> 0L);
  private final Evolver evolver = new SomeEvolver();
  private final EventRepo eventRepo = EventRepo.inMemory();

  @Override
  public Aggregate create(StateId stateId) {
    return Aggregate.create(stateId, decider, evolver, eventRepo);
  }
}
