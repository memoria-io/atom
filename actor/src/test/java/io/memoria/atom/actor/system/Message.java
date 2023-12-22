package io.memoria.atom.actor.system;

import io.memoria.atom.core.Shardable;
import io.memoria.atom.core.id.Id;

public record Message(Id shardKey) implements Shardable {
  public Message() {
    this(Id.of());
  }
}

