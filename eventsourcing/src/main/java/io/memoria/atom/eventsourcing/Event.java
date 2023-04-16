package io.memoria.atom.eventsourcing;

import io.memoria.atom.core.id.Id;

import java.io.Serializable;

public interface Event extends Shardable, Serializable {
  Id commandId();

  Id eventId();

  long timestamp();
}
