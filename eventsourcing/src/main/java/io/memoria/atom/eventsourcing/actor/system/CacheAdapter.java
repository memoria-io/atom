package io.memoria.atom.eventsourcing.actor.system;

import io.memoria.atom.eventsourcing.actor.StateActor;
import io.memoria.atom.eventsourcing.state.StateId;

import javax.cache.Cache;
import javax.cache.Cache.Entry;
import java.util.Iterator;
import java.util.function.Function;
import java.util.stream.StreamSupport;

class CacheAdapter implements ActorStore {
  private final Cache<StateId, StateActor> cache;

  public CacheAdapter(Cache<StateId, StateActor> cache) {
    this.cache = cache;
  }

  @Override
  public void computeIfAbsent(StateId stateId, Function<StateId, StateActor> actorFn) {
    cache.putIfAbsent(stateId, actorFn.apply(stateId));
  }

  @Override
  public StateActor get(StateId actorId) {
    return cache.get(actorId);
  }

  @Override
  public void close() {
    cache.close();
  }

  @Override
  public Iterator<StateActor> iterator() {
    return StreamSupport.stream(cache.spliterator(), false).map(Entry::getValue).iterator();
  }
}
