package io.memoria.atom.actor;

import io.memoria.atom.core.domain.Shardable;
import io.vavr.control.Try;

import java.util.function.Function;

public interface Actor extends Function<Shardable, Try<Shardable>> {
  ActorId actorId();
}
