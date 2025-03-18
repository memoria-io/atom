package io.memoria.atom.eventsourcing;

import io.memoria.atom.core.id.Id;
import io.memoria.atom.eventsourcing.aggregate.Aggregate;
import io.memoria.atom.eventsourcing.aggregate.Decider;
import io.memoria.atom.eventsourcing.aggregate.store.AggregateStore;
import io.memoria.atom.eventsourcing.event.repo.EventRepo;
import io.memoria.atom.eventsourcing.state.StateId;
import io.memoria.atom.eventsourcing.usecase.simple.SimpleDecider;
import io.memoria.atom.eventsourcing.usecase.simple.SimpleEvolver;

import javax.cache.Caching;
import javax.cache.configuration.MutableConfiguration;
import javax.cache.expiry.CreatedExpiryPolicy;
import javax.cache.expiry.Duration;
import java.util.concurrent.TimeUnit;

public class Utils {
  private Utils() {}

  public static Decider simpleDecider() {
    return new SimpleDecider(() -> Id.of(0), () -> 0L);
  }

  public static Aggregate simpleAggregate(StateId stateId) {
    return Aggregate.create(stateId, Utils.simpleDecider(), new SimpleEvolver(), EventRepo.inMemory());
  }

  public static AggregateStore cachedAggregateStore(String cacheName, int timeToLiveMillis) {
    Duration duration = new Duration(TimeUnit.MILLISECONDS, timeToLiveMillis);
    var policyFactory = CreatedExpiryPolicy.factoryOf(duration);
    var config = new MutableConfiguration<StateId, Aggregate>();
    config.setTypes(StateId.class, Aggregate.class).setStoreByValue(false).setExpiryPolicyFactory(policyFactory);

    var cache = Caching.getCachingProvider().getCacheManager().createCache(cacheName, config);
    return AggregateStore.cachedStore(cache);
  }
}
