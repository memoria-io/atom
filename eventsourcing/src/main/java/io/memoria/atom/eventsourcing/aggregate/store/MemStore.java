package io.memoria.atom.eventsourcing.aggregate.store;

import io.memoria.atom.eventsourcing.aggregate.Aggregate;
import io.memoria.atom.eventsourcing.state.StateId;

import java.util.Iterator;
import java.util.Map;
import java.util.function.Function;

class MemStore implements Store {
  private final Map<StateId, Aggregate> map;

  public MemStore(Map<StateId, Aggregate> map) {
    this.map = map;
  }

  @Override
  public void computeIfAbsent(StateId stateId, Function<StateId, Aggregate> actorFn) {
    map.computeIfAbsent(stateId, actorFn);
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
  public Iterator<Aggregate> iterator() {
    return map.values().iterator();
  }
}