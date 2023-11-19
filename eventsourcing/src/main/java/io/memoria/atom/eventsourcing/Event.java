package io.memoria.atom.eventsourcing;

import java.io.Serializable;

public interface Event extends Serializable {
  EventMeta meta();
}
