package io.memoria.atom.actor.system;

import io.memoria.atom.actor.Actor;
import io.memoria.atom.actor.ActorFactory;
import io.memoria.atom.actor.ActorId;
import io.memoria.atom.actor.Message;
import io.vavr.control.Try;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class MemActorSystem implements ActorSystem {
  private final ActorFactory actorFactory;
  private final Map<ActorId, Actor> map;

  public MemActorSystem(ActorFactory actorFactory) {
    this(actorFactory, new ConcurrentHashMap<>());
  }

  public MemActorSystem(ActorFactory actorFactory, Map<ActorId, Actor> map) {
    this.actorFactory = actorFactory;
    this.map = map;
  }

  @Override
  public Try<Message> handle(ActorId actorId, Message message) {
    map.computeIfAbsent(actorId, actorFactory::create);
    return map.get(actorId).apply(message);
  }
}