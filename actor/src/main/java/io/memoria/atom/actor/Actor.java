package io.memoria.atom.actor;

import io.memoria.atom.core.domain.Shardable;

import java.util.function.Function;

public interface Actor extends Function<Shardable, Shardable> {
  ActorId actorId();
}
