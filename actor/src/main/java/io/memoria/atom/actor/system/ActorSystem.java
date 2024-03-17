package io.memoria.atom.actor.system;

import io.memoria.atom.actor.Actor;
import io.memoria.atom.actor.ActorFactory;
import io.memoria.atom.actor.ActorId;
import io.memoria.atom.core.domain.Shardable;

import java.io.Closeable;
import java.util.function.BiFunction;

public interface ActorSystem extends Closeable, Iterable<Actor>, BiFunction<ActorId, Shardable, Shardable> {
  ActorStore actorStore();

  ActorFactory actorFactory();

  static ActorSystem create(ActorStore actorStore, ActorFactory actorFactory) {
    return new DefaultActorSystem(actorStore, actorFactory);
  }
}
