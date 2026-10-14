package com.marmoush.javakit.eventsourcing.usecase.banking.state;

import com.marmoush.javakit.eventsourcing.state.State;
import com.marmoush.javakit.eventsourcing.state.StateMeta;

public record ClosedAccount(StateMeta meta) implements Account {
  @Override
  public State withMeta(StateMeta meta) {
    return new ClosedAccount(meta);
  }
}
