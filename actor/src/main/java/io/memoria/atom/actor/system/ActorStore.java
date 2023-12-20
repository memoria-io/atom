package io.memoria.atom.actor.system;

import io.memoria.atom.actor.Actor;
import io.memoria.atom.actor.ActorId;

import javax.cache.Cache;
import java.io.Closeable;
import java.util.Map;
import java.util.function.Function;

public interface ActorStore extends Closeable, Iterable<Actor> {
  void computeIfAbsent(ActorId actorId, Function<ActorId, Actor> actorFn);

  Actor get(ActorId actorId);

  static ActorStore mapStore(Map<ActorId, Actor> map) {
    return new MapAdapter(map);
  }

  static ActorStore cacheStore(Cache<ActorId, Actor> cache) {
    return new CacheAdapter(cache);
  }
}
