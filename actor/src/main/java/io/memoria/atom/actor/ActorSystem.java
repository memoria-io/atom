package io.memoria.atom.actor;

import io.vavr.control.Try;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class ActorSystem {
  private final ActorFactory actorFactory;
  private final Map<ActorId, Actor> actorMap;

  public ActorSystem(ActorFactory actorFactory) {
    this(actorFactory, new ConcurrentHashMap<>());
  }

  public ActorSystem(ActorFactory actorFactory, Map<ActorId, Actor> actorMap) {
    this.actorFactory = actorFactory;
    this.actorMap = actorMap;
  }

  public Try<Message> handle(ActorId actorId, Message message) {
    actorMap.computeIfAbsent(actorId, actorFactory::create);
    return actorMap.get(actorId).apply(message);
  }
}
