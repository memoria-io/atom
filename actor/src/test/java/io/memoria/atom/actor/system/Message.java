package io.memoria.atom.actor.system;

import io.memoria.atom.core.domain.Shardable;
import io.memoria.atom.core.id.Id;

import java.util.UUID;

public record Message(Id shardKey) implements Shardable {
  public Message() {
    this(Id.of(UUID.randomUUID()));
  }
}

