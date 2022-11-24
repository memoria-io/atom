package io.memoria.atom.active.repo.mem;

import io.memoria.atom.core.eventsourcing.Event;

import java.util.ArrayList;
import java.util.List;

record MemEventPartition<E extends Event>(List<E> msgs) {
  public MemEventPartition() {
    this(new ArrayList<>());
  }
}
