package com.marmoush.atom.eventsourcing.command;

import com.marmoush.atom.core.id.Id;

import java.util.UUID;

public class CommandIds {
  private CommandIds() {}

  public static CommandId of(String value) {
    return new CommandId(value);
  }

  public static CommandId of(long value) {
    return new CommandId(value);
  }

  public static CommandId of(UUID uuid) {
    return new CommandId(uuid.toString());
  }

  public static CommandId of(Id id) {
    return new CommandId(id);
  }
}
