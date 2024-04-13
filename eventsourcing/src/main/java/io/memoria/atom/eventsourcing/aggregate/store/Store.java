package io.memoria.atom.eventsourcing.aggregate.store;

import io.memoria.atom.eventsourcing.aggregate.Aggregate;
import io.memoria.atom.eventsourcing.state.StateId;

import javax.cache.Cache;
import java.io.Closeable;
import java.util.Map;
import java.util.function.Function;

public interface Store extends Closeable, Iterable<Aggregate> {
  void computeIfAbsent(StateId stateId, Function<StateId, Aggregate> actorFn);

  Aggregate get(StateId stateId);

  void remove(StateId stateId);

  static Store mapStore(Map<StateId, Aggregate> map) {
    return new MemStore(map);
  }

  static Store cacheStore(Cache<StateId, Aggregate> cache) {
    return new CachedStore(cache);
  }
}
