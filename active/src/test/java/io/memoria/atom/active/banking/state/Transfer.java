package io.memoria.atom.active.banking.state;

import io.memoria.atom.core.eventsourcing.StateId;
import io.memoria.atom.core.id.Id;

import java.io.Serializable;

public record Transfer(Id id, StateId sender, StateId receiver, int amount) implements Serializable {
  public Transfer {
    if (amount < 1) {
      throw new IllegalArgumentException("Amount can't be less than 1");
    }
  }
}
