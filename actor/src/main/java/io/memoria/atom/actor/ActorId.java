package io.memoria.atom.actor;

import io.memoria.atom.core.id.Id;

import java.io.Serializable;
import java.util.UUID;

public record ActorId(Id id) implements Comparable<ActorId>, Serializable {
  public String value() {
    return id().value();
  }

  public static ActorId of() {
    return new ActorId(Id.of());
  }

  public static ActorId of(Id id) {
    return new ActorId(id);
  }

  public static ActorId of(UUID id) {
    return new ActorId(Id.of(id));
  }

  public static ActorId of(long i) {
    return new ActorId(Id.of(i));
  }

  public static ActorId of(String value) {
    return new ActorId(Id.of(value));
  }

  @Override
  public int compareTo(ActorId o) {
    return o.id.compareTo(id);
  }
}
