package io.memoria.atom.eventsourcing.usecase.simple;

import io.memoria.atom.eventsourcing.state.State;
import io.memoria.atom.eventsourcing.state.StateMeta;

public record SimpleState(StateMeta meta) implements State {
  @Override
  public State withMeta(StateMeta meta) {
    return new SimpleState(meta);
  }
}
