package com.marmoush.atom.eventsourcing.usecase.simple;

import com.marmoush.atom.eventsourcing.state.State;
import com.marmoush.atom.eventsourcing.state.StateMeta;

public record SimpleState(StateMeta meta) implements State {
  @Override
  public State withMeta(StateMeta meta) {
    return new SimpleState(meta);
  }
}
