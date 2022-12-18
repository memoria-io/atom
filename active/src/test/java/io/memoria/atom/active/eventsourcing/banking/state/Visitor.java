package io.memoria.atom.active.eventsourcing.banking.state;

import io.memoria.atom.core.eventsourcing.StateId;

public record Visitor(StateId stateId) implements User {
  public Visitor() {
    this(StateId.randomUUID());
  }
}
