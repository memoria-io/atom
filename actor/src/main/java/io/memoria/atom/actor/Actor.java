package io.memoria.atom.actor;

import io.memoria.atom.core.domain.Partitioned;

public interface Actor {
  ActorId actorId();

  Partitioned apply(Partitioned partitioned) throws ActorException;
}
