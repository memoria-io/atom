package io.memoria.atom.eventsourcing;

import java.io.Serializable;

public interface Command extends Serializable {
  CommandMeta meta();
}
