package com.marmoush.atom.eventsourcing.aggregate;

import com.marmoush.atom.core.id.Ids;
import com.marmoush.atom.eventsourcing.event.repo.EventRepo;
import com.marmoush.atom.eventsourcing.state.StateId;
import com.marmoush.atom.eventsourcing.usecase.simple.SimpleDecider;
import com.marmoush.atom.eventsourcing.usecase.simple.SimpleEvolver;

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
