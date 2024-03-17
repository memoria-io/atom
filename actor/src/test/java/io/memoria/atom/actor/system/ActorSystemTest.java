package io.memoria.atom.actor.system;

import io.memoria.atom.actor.Actor;
import io.memoria.atom.actor.ActorId;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import javax.cache.Caching;
import javax.cache.configuration.MutableConfiguration;
import java.io.IOException;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CountDownLatch;
import java.util.stream.IntStream;
import java.util.stream.LongStream;
import java.util.stream.Stream;

public class ActorSystemTest {
  private static final int numOfActors = 1000;
  private static final int numOfRequests = 1000;

  @ParameterizedTest
  @MethodSource("testArgs")
  void syncTest(ActorStore actorStore, CountDownLatch latch) throws InterruptedException {
    //    System.out.printf("Handling total %d requests", latch.getCount());
    try (var actorSystem = ActorSystem.create(actorStore, new DomainActorFactory(latch))) {
      LongStream.range(0, numOfActors).mapToObj(ActorId::new).forEach(actorId -> startActor(actorId, actorSystem));
      latch.await();
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }

  private static void startActor(ActorId actorId, ActorSystem actorSystem) {
    Thread.ofVirtual().start(() -> {
      //      System.out.println("Starting: " + actorId);
      IntStream.range(0, numOfRequests).forEach(_ -> {
        actorSystem.apply(actorId, new Message());
      });
    });
  }

  private static ActorStore cachedActorStore() {
    var config = new MutableConfiguration<ActorId, Actor>().setTypes(ActorId.class, Actor.class).setStoreByValue(false);
    //.setExpiryPolicyFactory(CreatedExpiryPolicy.factoryOf(Duration.ONE_MINUTE));
    var cache = Caching.getCachingProvider().getCacheManager().createCache("simpleCache", config);
    return ActorStore.cacheStore(cache);
  }

  private static ActorStore mapActorStore() {
    return ActorStore.mapStore(new ConcurrentHashMap<>());
  }

  private static Stream<Arguments> testArgs() {
    return Stream.of(Arguments.of(mapActorStore(), createLatch()), Arguments.of(cachedActorStore(), createLatch()));
  }

  private static CountDownLatch createLatch() {
    return new CountDownLatch(numOfActors * numOfRequests);
  }
}
