package io.memoria.atom.actor;

import io.memoria.atom.core.id.Id;

import java.util.UUID;

public class ActorId extends Id {
  public ActorId(String value) {
    super(value);
  }

  public ActorId(long value) {
    super(value);
  }

  public ActorId(UUID value) {
    super(value);
  }

  public ActorId(Id id) {
    super(id);
  }

  public static ActorId of(Id id) {
    return new ActorId(id);
  }
}
