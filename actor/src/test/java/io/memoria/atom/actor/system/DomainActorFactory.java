package io.memoria.atom.actor.system;

import io.memoria.atom.actor.Actor;
import io.memoria.atom.actor.ActorFactory;
import io.memoria.atom.actor.ActorId;

import java.util.concurrent.CountDownLatch;

class DomainActorFactory implements ActorFactory {
  private final CountDownLatch latch;

  DomainActorFactory(CountDownLatch latch) {this.latch = latch;}

  @Override
  public Actor create(ActorId id) {
    return new MyActor(id, latch);
  }
}
