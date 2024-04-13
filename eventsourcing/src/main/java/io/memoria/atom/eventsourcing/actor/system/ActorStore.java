package io.memoria.atom.eventsourcing.actor.system;

import io.memoria.atom.eventsourcing.actor.StateAggregate;
import io.memoria.atom.eventsourcing.state.StateId;

import javax.cache.Cache;
import java.io.Closeable;
import java.util.Map;
import java.util.function.Function;

public interface ActorStore extends Closeable, Iterable<StateAggregate> {
  void computeIfAbsent(StateId stateId, Function<StateId, StateAggregate> actorFn);

  StateAggregate get(StateId actorId);

  static ActorStore mapStore(Map<StateId, StateAggregate> map) {
    return new MapAdapter(map);
  }

  static ActorStore cacheStore(Cache<StateId, StateAggregate> cache) {
    return new CacheAdapter(cache);
  }
}
