package io.memoria.atom.eventsourcing.actor.system;

import io.memoria.atom.eventsourcing.actor.StateAggregate;
import io.memoria.atom.eventsourcing.state.StateId;

import java.util.Iterator;
import java.util.Map;
import java.util.function.Function;

class MapAdapter implements ActorStore {
  private final Map<StateId, StateAggregate> map;

  public MapAdapter(Map<StateId, StateAggregate> map) {
    this.map = map;
  }

  @Override
  public void computeIfAbsent(StateId stateId, Function<StateId, StateAggregate> actorFn) {
    map.computeIfAbsent(stateId, actorFn);
  }

  @Override
  public StateAggregate get(StateId actorId) {
    return map.get(actorId);
  }

  @Override
  public void close() {
    // Silence is golden
  }

  @Override
  public Iterator<StateAggregate> iterator() {
    return map.values().iterator();
  }
}