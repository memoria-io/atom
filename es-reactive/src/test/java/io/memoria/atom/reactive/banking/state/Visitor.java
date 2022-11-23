package io.memoria.atom.reactive.banking.state;

import io.memoria.atom.core.eventsourcing.StateId;

public record Visitor(StateId stateId) implements User {
  public Visitor() {
    this(StateId.randomUUID());
  }
}
