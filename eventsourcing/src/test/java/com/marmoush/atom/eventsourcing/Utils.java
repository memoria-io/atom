package com.marmoush.atom.eventsourcing;

import com.marmoush.atom.core.id.Ids;
import com.marmoush.atom.eventsourcing.aggregate.Aggregate;
import com.marmoush.atom.eventsourcing.aggregate.Decider;
import com.marmoush.atom.eventsourcing.event.repo.EventRepo;
import com.marmoush.atom.eventsourcing.state.StateId;
import com.marmoush.atom.eventsourcing.usecase.simple.SimpleDecider;
import com.marmoush.atom.eventsourcing.usecase.simple.SimpleEvolver;

public class Utils {
  private Utils() {}

  public static Decider simpleDecider() {
    return new SimpleDecider(() -> Ids.of(0), () -> 0L);
  }

  public static Aggregate simpleAggregate(StateId stateId) {
    return Aggregate.create(stateId, Utils.simpleDecider(), new SimpleEvolver(), EventRepo.inMemory());
  }
}
