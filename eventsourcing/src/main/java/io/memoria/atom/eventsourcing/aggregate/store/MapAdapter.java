package io.memoria.atom.eventsourcing.aggregate.store;

import io.memoria.atom.eventsourcing.aggregate.Aggregate;
import io.memoria.atom.eventsourcing.state.StateId;

import java.util.Iterator;
import java.util.Map;
import java.util.function.Function;

class MapAdapter implements AggregateStore {
  private final Map<StateId, Aggregate> map;

  public MapAdapter(Map<StateId, Aggregate> map) {
    this.map = map;
  }

  @Override
  public void computeIfAbsent(StateId stateId, Function<StateId, Aggregate> actorFn) {
    map.computeIfAbsent(stateId, actorFn);
  }

  @Override
  public Aggregate get(StateId actorId) {
    return map.get(actorId);
  }

  @Override
  public void close() {
    // Silence is golden
  }

  @Override
  public Iterator<Aggregate> iterator() {
    return map.values().iterator();
  }
}