package io.memoria.atom.eventsourcing.aggregate.store;

import io.memoria.atom.eventsourcing.aggregate.Aggregate;
import io.memoria.atom.eventsourcing.state.StateId;

import javax.cache.Cache;
import java.io.Closeable;
import java.util.Map;
import java.util.function.Function;

public interface AggregateStore extends Closeable, Iterable<Aggregate> {
  void computeIfAbsent(StateId stateId, Function<StateId, Aggregate> actorFn);

  Aggregate get(StateId actorId);

  static AggregateStore mapStore(Map<StateId, Aggregate> map) {
    return new MapAdapter(map);
  }

  static AggregateStore cacheStore(Cache<StateId, Aggregate> cache) {
    return new CacheAdapter(cache);
  }
}
