package io.memoria.atom.actor.system;

import io.memoria.atom.actor.Actor;
import io.memoria.atom.actor.ActorId;

import javax.cache.Cache;
import javax.cache.Cache.Entry;
import java.io.IOException;
import java.util.Iterator;
import java.util.function.Function;
import java.util.stream.StreamSupport;

class CacheAdapter implements ActorStore {
  private final Cache<ActorId, Actor> cache;

  public CacheAdapter(Cache<ActorId, Actor> cache) {
    this.cache = cache;
  }

  @Override
  public void computeIfAbsent(ActorId actorId, Function<ActorId, Actor> actorFn) {
    cache.putIfAbsent(actorId, actorFn.apply(actorId));
  }

  @Override
  public Actor get(ActorId actorId) {
    return cache.get(actorId);
  }

  @Override
  public void close() throws IOException {
    cache.close();
  }

  @Override
  public Iterator<Actor> iterator() {
    return StreamSupport.stream(cache.spliterator(), false).map(Entry::getValue).iterator();
  }
}
