package io.memoria.atom.actor;

import io.memoria.atom.core.domain.Shardable;

public interface Actor {
  ActorId actorId();

  Shardable apply(Shardable shardable) throws ActorException;
}
