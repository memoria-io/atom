package io.memoria.atom.eventsourcing.aggregate.store;

import io.memoria.atom.eventsourcing.aggregate.Aggregate;
import io.memoria.atom.eventsourcing.state.StateId;

import javax.cache.Cache;
import javax.cache.Cache.Entry;
import java.util.Iterator;
import java.util.function.Function;
import java.util.stream.StreamSupport;

class CachedAggregateStore implements AggregateStore {
  private final Cache<StateId, Aggregate> cache;

  public CachedAggregateStore(Cache<StateId, Aggregate> cache) {
    this.cache = cache;
  }

  @Override
  public void computeIfAbsent(StateId stateId, Function<StateId, Aggregate> lazyActorFn) {
    cache.putIfAbsent(stateId, lazyActorFn.apply(stateId));
  }

  @Override
  public Aggregate get(StateId stateId) {
    return cache.get(stateId);
  }

  @Override
  public void remove(StateId stateId) {
    cache.remove(stateId);
  }

  @Override
  public void clear() {
    cache.clear();
  }

  @Override
  public Iterator<Aggregate> iterator() {
    return StreamSupport.stream(cache.spliterator(), false).map(Entry::getValue).iterator();
  }
}
