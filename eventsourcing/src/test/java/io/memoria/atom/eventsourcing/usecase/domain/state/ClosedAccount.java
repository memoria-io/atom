package io.memoria.atom.eventsourcing.usecase.domain.state;

import io.memoria.atom.eventsourcing.state.State;
import io.memoria.atom.eventsourcing.state.StateMeta;

public record ClosedAccount(StateMeta meta) implements Account {
  @Override
  public State withMeta(StateMeta meta) {
    return new ClosedAccount(meta);
  }
}
