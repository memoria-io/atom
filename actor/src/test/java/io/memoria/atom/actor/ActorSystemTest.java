package io.memoria.atom.actor;

import io.vavr.Tuple;
import io.vavr.collection.HashMap;
import io.vavr.collection.List;
import io.vavr.control.Try;
import org.junit.jupiter.api.Test;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CountDownLatch;

public class ActorSystemTest {
  private final int numOfActors = 9;
  private final int numOfRequests = 1111;
  private final CountDownLatch latch = new CountDownLatch(numOfRequests * numOfActors);
  private final ActorFactory actorFactory = new DomainActorFactory(latch);
  private final Map<ActorId, Actor> actorMap = new ConcurrentHashMap<>();
  private final ActorSystem actorSystem = new ActorSystem(actorFactory, actorMap);

  @Test
  void syncTest() throws InterruptedException {
    // Create numOfRequests and spread evenly across numOfActors
    List.range(0, numOfRequests)
        .flatMap(reqId -> List.range(0, numOfActors).map(i -> Tuple.of(reqId, ActorId.of(i))))
        .shuffle()
        .forEach(tup -> handle(tup._1, tup._2));
    latch.await();
    assert HashMap.ofAll(actorMap)
                  .map(tup -> tup._2)
                  .map(actor -> (MyActor) actor)
                  .forAll(actor -> actor.getCount() == numOfRequests);
  }

  private void handle(int threadId, ActorId actorId) {
    Thread.ofVirtual().name("Thread:" + threadId).start(() -> actorSystem.handle(actorId, new Message()));
  }

  static class DomainActorFactory implements ActorFactory {
    private final CountDownLatch latch;

    public DomainActorFactory(CountDownLatch latch) {
      this.latch = latch;
    }

    @Override
    public Actor create(ActorId id) {
      return new MyActor(id, latch);
    }
  }

  static class MyActor extends AbstractActor {
    private final CountDownLatch latch;
    private volatile int count;

    MyActor(ActorId actorId, CountDownLatch latch) {
      super(actorId);
      this.latch = latch;
      this.count = 0;
    }

    public int getCount() {
      return count;
    }

    @Override
    public synchronized Try<Message> apply(Message message) {
      //      try {
      count++;
      //      System.out.println(STR. "Hello from: \{ Thread.currentThread().getName() } Count is now: \{ count }" );
      //        Thread.sleep(r.nextInt(1000));
      //      count--;
      latch.countDown();
      //      System.out.println(STR. "Hello from: \{ Thread.currentThread().getName() } Count is now: \{ count }" );
      return Try.success(message);
      //      } catch (InterruptedException e) {
      //        return Try.failure(e);
      //      }
    }
  }
}
