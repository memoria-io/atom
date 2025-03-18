package io.memoria.atom.eventsourcing.aggregate.store;

import io.memoria.atom.eventsourcing.aggregate.Aggregate;
import io.memoria.atom.eventsourcing.state.StateId;

import java.util.Iterator;
import java.util.Map;
import java.util.function.Function;

class MemAggregateStore implements AggregateStore {
  private final Map<StateId, Aggregate> map;

  public MemAggregateStore(Map<StateId, Aggregate> map) {
    this.map = map;
  }

  @Override
  public void computeIfAbsent(StateId stateId, Function<StateId, Aggregate> lazyActorFn) {
    map.computeIfAbsent(stateId, lazyActorFn);
  }

  @Override
  public Aggregate get(StateId stateId) {
    return map.get(stateId);
  }

  @Override
  public void remove(StateId stateId) {
    map.remove(stateId);
  }

  @Override
  public void clear() {
    map.clear();
  }

  @Override
  public Iterator<Aggregate> iterator() {
    return map.values().iterator();
  }
}