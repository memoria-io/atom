package io.memoria.atom.reactive.repo.mem;

import io.memoria.atom.core.eventsourcing.Command;

import java.util.concurrent.LinkedBlockingDeque;

record MemCommandPartition<C extends Command>(LinkedBlockingDeque<C> msgs) {
  public MemCommandPartition() {
    this(new LinkedBlockingDeque<>());
  }
}
