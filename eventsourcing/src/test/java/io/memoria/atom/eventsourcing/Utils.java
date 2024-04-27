package io.memoria.atom.eventsourcing;

import io.memoria.atom.core.id.Id;
import io.memoria.atom.eventsourcing.aggregate.Aggregate;
import io.memoria.atom.eventsourcing.aggregate.Decider;
import io.memoria.atom.eventsourcing.aggregate.store.AggregateStore;
import io.memoria.atom.eventsourcing.event.repo.EventRepo;
import io.memoria.atom.eventsourcing.state.StateId;
import io.memoria.atom.eventsourcing.usecase.simple.SimpleDecider;
import io.memoria.atom.eventsourcing.usecase.simple.SimpleEvolver;
import org.ehcache.CacheManager;
import org.ehcache.config.builders.CacheConfigurationBuilder;
import org.ehcache.config.builders.CacheManagerBuilder;
import org.ehcache.config.builders.ExpiryPolicyBuilder;
import org.ehcache.config.builders.ResourcePoolsBuilder;

import java.time.Duration;

public class Utils {
  public static final CacheManager cacheManager = CacheManagerBuilder.newCacheManagerBuilder().build();

  static {
    cacheManager.init();
  }

  private Utils() {}

  public static Decider simpleDecider() {
    return new SimpleDecider(() -> Id.of(0), () -> 0L);
  }

  public static Aggregate simpleAggregate(StateId stateId) {
    return Aggregate.create(stateId, Utils.simpleDecider(), new SimpleEvolver(), EventRepo.inMemory());
  }

  public static AggregateStore cachedAggregateStore(String cacheName, int heapSize, int timeToLiveMillis) {
    var heap = ResourcePoolsBuilder.heap(heapSize).build();
    var expiry = ExpiryPolicyBuilder.timeToLiveExpiration(Duration.ofMillis(timeToLiveMillis));
    var configBuilder = CacheConfigurationBuilder.newCacheConfigurationBuilder(StateId.class, Aggregate.class, heap)
                                                 .withExpiry(expiry);
    var cache = cacheManager.createCache(cacheName, configBuilder);
    return new CachedAggregateStore(cache);
  }
}
