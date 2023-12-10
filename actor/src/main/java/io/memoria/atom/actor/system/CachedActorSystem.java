package io.memoria.atom.actor.system;

import io.memoria.atom.actor.Actor;
import io.memoria.atom.actor.ActorFactory;
import io.memoria.atom.actor.ActorId;
import io.memoria.atom.actor.Message;
import io.vavr.control.Try;

import javax.cache.Cache;

public class CachedActorSystem implements ActorSystem {
  private final ActorFactory actorFactory;
  private final Cache<ActorId, Actor> cache;

  public CachedActorSystem(ActorFactory actorFactory, Cache<ActorId, Actor> cache) {
    this.actorFactory = actorFactory;
    this.cache = cache;
  }

  @Override
  public Try<Message> handle(ActorId actorId, Message message) {
    cache.putIfAbsent(actorId, actorFactory.create(actorId));
    return cache.get(actorId).apply(message);
  }
}
