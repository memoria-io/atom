package io.memoria.atom.core.eventsourcing;

import java.io.Serializable;

public interface Event extends Shardable, Serializable {
  CommandId commandId();

  EventId eventId();

  long timestamp();
}
