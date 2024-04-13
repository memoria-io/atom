package io.memoria.atom.eventsourcing.handler;

import io.memoria.atom.core.id.Id;
import io.memoria.atom.eventsourcing.rule.Decider;
import io.memoria.atom.eventsourcing.rule.Evolver;
import io.memoria.atom.eventsourcing.rule.SomeDecider;
import io.memoria.atom.eventsourcing.rule.SomeEvolver;
import io.memoria.atom.eventsourcing.state.StateId;

import java.util.UUID;

public class DomainAggregateFactory implements AggregateFactory {
  private final Decider decider = new SomeDecider(() -> Id.of(UUID.randomUUID()), () -> 0L);
  private final Evolver evolver = new SomeEvolver();

  @Override
  public DefaultCommandHandler create(StateId stateId) {
    return DefaultCommandHandler.create(decider, evolver, stateId);
  }
}
