package io.memoria.atom.eventsourcing;

import java.io.Serializable;

public interface Event extends Shardable, Serializable {
  CommandId commandId();

  EventId eventId();

  long timestamp();
}
