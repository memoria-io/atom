package io.memoria.atom.eventsourcing;

import io.memoria.atom.core.id.Ids;
import io.memoria.atom.eventsourcing.aggregate.Aggregate;
import io.memoria.atom.eventsourcing.aggregate.Decider;
import io.memoria.atom.eventsourcing.event.repo.EventRepo;
import io.memoria.atom.eventsourcing.state.StateId;
import io.memoria.atom.eventsourcing.usecase.simple.SimpleDecider;
import io.memoria.atom.eventsourcing.usecase.simple.SimpleEvolver;

public class Utils {
  private Utils() {}

  public static Decider simpleDecider() {
    return new SimpleDecider(() -> Ids.of(0), () -> 0L);
  }

  public static Aggregate simpleAggregate(StateId stateId) {
    return Aggregate.create(stateId, Utils.simpleDecider(), new SimpleEvolver(), EventRepo.inMemory());
  }
}
