//package io.memoria.atom.eventsourcing.actor;
//
//import io.memoria.atom.actor.AbstractActor;
//import io.memoria.atom.actor.ActorId;
//import io.memoria.atom.core.domain.Partitioned;
//
//import java.util.List;
//import java.util.concurrent.CountDownLatch;
//
//class MyActor extends AbstractActor {
//  private final CountDownLatch latch;
//
//  MyActor(ActorId actorId, CountDownLatch latch) {
//    super(actorId);
//    this.latch = latch;
//  }
//
//  @Override
//  public List<Partitioned> init() {
//    return null;
//  }
//
//  @Override
//  public synchronized Partitioned handle(Partitioned message) {
//    latch.countDown();
//    return message;
//  }
//}
