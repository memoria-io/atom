package io.memoria.atom.eventsourcing.actor.system;

import io.memoria.atom.eventsourcing.actor.StateActor;
import io.memoria.atom.eventsourcing.state.StateId;

import javax.cache.Cache;
import java.io.Closeable;
import java.util.Map;
import java.util.function.Function;

public interface ActorStore extends Closeable, Iterable<StateActor> {
  void computeIfAbsent(StateId stateId, Function<StateId, StateActor> actorFn);

  StateActor get(StateId actorId);

  static ActorStore mapStore(Map<StateId, StateActor> map) {
    return new MapAdapter(map);
  }

  static ActorStore cacheStore(Cache<StateId, StateActor> cache) {
    return new CacheAdapter(cache);
  }
}
