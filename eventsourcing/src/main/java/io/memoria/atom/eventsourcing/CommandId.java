package io.memoria.atom.eventsourcing;

import io.memoria.atom.core.id.Id;

public class CommandId extends Id {
  public CommandId(Id id) {
    super(id);
  }
}
