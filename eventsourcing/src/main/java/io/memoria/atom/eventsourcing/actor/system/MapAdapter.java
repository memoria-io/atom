package io.memoria.atom.eventsourcing.actor.system;

import io.memoria.atom.eventsourcing.actor.StateActor;
import io.memoria.atom.eventsourcing.state.StateId;

import java.util.Iterator;
import java.util.Map;
import java.util.function.Function;

class MapAdapter implements ActorStore {
  private final Map<StateId, StateActor> map;

  public MapAdapter(Map<StateId, StateActor> map) {
    this.map = map;
  }

  @Override
  public void computeIfAbsent(StateId stateId, Function<StateId, StateActor> actorFn) {
    map.computeIfAbsent(stateId, actorFn);
  }

  @Override
  public StateActor get(StateId actorId) {
    return map.get(actorId);
  }

  @Override
  public void close() {
    // Silence is golden
  }

  @Override
  public Iterator<StateActor> iterator() {
    return map.values().iterator();
  }
}