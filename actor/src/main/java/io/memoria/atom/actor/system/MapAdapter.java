package io.memoria.atom.actor.system;

import io.memoria.atom.actor.Actor;
import io.memoria.atom.actor.ActorId;

import java.util.Iterator;
import java.util.Map;
import java.util.function.Function;

class MapAdapter implements ActorStore {
  private final Map<ActorId, Actor> map;

  public MapAdapter(Map<ActorId, Actor> map) {
    this.map = map;
  }

  @Override
  public void computeIfAbsent(ActorId actorId, Function<ActorId, Actor> actorFn) {
    map.computeIfAbsent(actorId, actorFn);
  }

  @Override
  public Actor get(ActorId actorId) {
    return map.get(actorId);
  }

  @Override
  public void close() {
    // Silence is golden
  }

  @Override
  public Iterator<Actor> iterator() {
    return map.values().iterator();
  }
}