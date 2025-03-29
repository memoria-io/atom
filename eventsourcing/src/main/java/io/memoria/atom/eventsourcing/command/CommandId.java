package io.memoria.atom.eventsourcing.command;

import io.memoria.atom.core.id.Id;

import java.util.UUID;

public class CommandId extends Id {
  public CommandId(String value) {
    super(value);
  }

  public CommandId(long value) {
    super(value);
  }

  public CommandId(UUID value) {
    super(value);
  }

  public CommandId(Id id) {
    super(id);
  }
}
