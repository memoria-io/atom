package io.memoria.atom.eventsourcing.aggregate;

import io.memoria.atom.eventsourcing.aggregate.store.Store;
import io.memoria.atom.eventsourcing.state.StateId;
import org.junit.jupiter.api.Named;
import org.junit.jupiter.params.provider.Arguments;

import javax.cache.Caching;
import javax.cache.configuration.MutableConfiguration;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Stream;

public class Utils {
  private Utils(){}

  public static Stream<Arguments> stores() {
    return Stream.of(Arguments.of(Named.of("Concurrent map store", Store.mapStore())),
                     Arguments.of(Named.of("Cache store", cachedActorStore())));
  }

  private static Store createStore() {
    return Store.mapStore(new ConcurrentHashMap<>());
  }

  private static Store cachedActorStore() {
    var config = new MutableConfiguration<StateId, Aggregate>().setTypes(StateId.class, Aggregate.class)
                                                               .setStoreByValue(false);
    //.setExpiryPolicyFactory(CreatedExpiryPolicy.factoryOf(Duration.ONE_MINUTE));
    var cache = Caching.getCachingProvider().getCacheManager().createCache("simpleCache", config);
    return Store.cacheStore(cache);
  }
}
