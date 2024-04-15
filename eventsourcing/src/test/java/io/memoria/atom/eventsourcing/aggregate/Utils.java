package io.memoria.atom.eventsourcing.aggregate;

import io.memoria.atom.eventsourcing.aggregate.store.Store;
import io.memoria.atom.eventsourcing.state.StateId;

import javax.cache.Caching;
import javax.cache.configuration.MutableConfiguration;

public class Utils {
  private Utils() {}

  public static Store cachedActorStore(String cacheName) {
    var config = new MutableConfiguration<StateId, Aggregate>().setTypes(StateId.class, Aggregate.class)
                                                               .setStoreByValue(false);
    //.setExpiryPolicyFactory(CreatedExpiryPolicy.factoryOf(Duration.ONE_MINUTE));
    var cache = Caching.getCachingProvider().getCacheManager().createCache(cacheName, config);
    return Store.cacheStore(cache);
  }
}