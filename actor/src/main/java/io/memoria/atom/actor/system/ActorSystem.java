package io.memoria.atom.actor.system;

import io.memoria.atom.actor.Actor;
import io.memoria.atom.actor.ActorException;
import io.memoria.atom.actor.ActorFactory;
import io.memoria.atom.actor.ActorId;
import io.memoria.atom.core.domain.Shardable;

import java.io.Closeable;

public interface ActorSystem extends Closeable, Iterable<Actor> {
  ActorStore actorStore();

  ActorFactory actorFactory();

  Shardable apply(ActorId actorId, Shardable shardable) throws ActorException;

  static ActorSystem create(ActorStore actorStore, ActorFactory actorFactory) {
    return new DefaultActorSystem(actorStore, actorFactory);
  }
}
