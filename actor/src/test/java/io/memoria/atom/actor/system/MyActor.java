package io.memoria.atom.actor.system;

import io.memoria.atom.actor.AbstractActor;
import io.memoria.atom.actor.ActorId;
import io.memoria.atom.core.domain.Shardable;


import java.util.concurrent.CountDownLatch;

class MyActor extends AbstractActor {
  private final CountDownLatch latch;

  MyActor(ActorId actorId, CountDownLatch latch) {
    super(actorId);
    this.latch = latch;
  }

  @Override
  public synchronized Try<Shardable> apply(Shardable message) {
    latch.countDown();
    return Try.success(message);
  }
}
