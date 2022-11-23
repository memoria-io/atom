package io.memoria.atom.es.active.banking.state;

import io.memoria.atom.core.id.Id;
import io.memoria.atom.core.eventsourcing.StateId;

import java.io.Serializable;

public record Transfer(Id id, StateId sender, StateId receiver, int amount) implements Serializable {
  public Transfer {
    if (amount < 1) {
      throw new IllegalArgumentException("Amount can't be less than 1");
    }
  }
}
