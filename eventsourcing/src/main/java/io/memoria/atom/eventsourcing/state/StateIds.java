package io.memoria.atom.eventsourcing.state;

import io.memoria.atom.core.id.Id;

import java.util.UUID;

public class StateIds {
  private StateIds() {}

  public static StateId of(String value) {
    return new StateId(value);
  }

  public static StateId of(long value) {
    return new StateId(value);
  }

  public static StateId of(UUID uuid) {
    return new StateId(uuid.toString());
  }

  public static StateId of(Id id) {
    return new StateId(id);
  }
}
