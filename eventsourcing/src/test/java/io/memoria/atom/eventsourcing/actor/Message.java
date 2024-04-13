package io.memoria.atom.eventsourcing.actor;

import io.memoria.atom.core.domain.Partitioned;
import io.memoria.atom.core.id.Id;

import java.util.UUID;

public record Message(Id pKey) implements Partitioned {
  public Message() {
    this(Id.of(UUID.randomUUID()));
  }
}

