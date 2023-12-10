package io.memoria.atom.actor.system;

import io.memoria.atom.actor.AbstractActor;
import io.memoria.atom.actor.Actor;
import io.memoria.atom.actor.ActorFactory;
import io.memoria.atom.actor.ActorId;
import io.memoria.atom.actor.Message;
import io.vavr.Tuple;
import io.vavr.collection.List;
import io.vavr.control.Try;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;

import javax.cache.Cache;
import javax.cache.Caching;
import javax.cache.configuration.MutableConfiguration;
import java.util.Objects;
import java.util.concurrent.CountDownLatch;

public class CachedActorSystemTest {
  private static final int numOfActors = 9;
  private static final int numOfRequests = 1111;

  private final Cache<ActorId, Actor> cache;
  private final CountDownLatch latch;
  private final ActorSystem actorSystem;

  public CachedActorSystemTest() {
    var cacheManager = Caching.getCachingProvider().getCacheManager();
    var config = new MutableConfiguration<ActorId, Actor>().setTypes(ActorId.class, Actor.class).setStoreByValue(false);
    //.setExpiryPolicyFactory(CreatedExpiryPolicy.factoryOf(Duration.ONE_MINUTE));
    cache = cacheManager.createCache("simpleCache", config);
    latch = new CountDownLatch(numOfRequests * numOfActors);
    actorSystem = new CachedActorSystem(new DomainActorFactory(latch), cache);
  }

  @Test
  void syncTest() throws InterruptedException {
    // Create numOfRequests and spread evenly across numOfActors
    List.range(0, numOfRequests)
        .flatMap(reqId -> List.range(0, numOfActors).map(i -> Tuple.of(reqId, ActorId.of(i))))
        .shuffle()
        .forEach(tup -> handle(tup._1, tup._2));
    latch.await();
    cache.iterator().forEachRemaining(entry -> {
      Assertions.assertThat(((MyActor) entry.getValue()).count).isEqualTo(numOfRequests);
    });
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
