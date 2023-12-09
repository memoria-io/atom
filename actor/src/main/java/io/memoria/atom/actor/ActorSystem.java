package io.memoria.atom.actor;

import io.memoria.atom.core.Shardable;
import io.memoria.atom.core.id.Id;
import io.vavr.control.Try;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class ActorSystem implements Shardable {
  private final Id shardKey;
  private final ActorFactory actorFactory;
  private final Map<Id, Actor> actorMap;

  public ActorSystem(ActorFactory actorFactory) {
    this(Id.of(), actorFactory, new ConcurrentHashMap<>());
  }

  public ActorSystem(Id shardKey, ActorFactory actorFactory) {
    this(shardKey, actorFactory, new ConcurrentHashMap<>());
  }

  public ActorSystem(Id shardKey, ActorFactory actorFactory, Map<Id, Actor> actorMap) {
    this.shardKey = shardKey;
    this.actorFactory = actorFactory;
    this.actorMap = actorMap;
  }

  public Try<Message> handle(Id actorId, Message message) {
    actorMap.computeIfAbsent(actorId, actorFactory::create);
    return actorMap.get(actorId).apply(message);
  }

  @Override
  public Id shardKey() {
    return shardKey;
  }
}
