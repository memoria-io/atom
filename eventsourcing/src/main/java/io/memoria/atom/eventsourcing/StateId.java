package io.memoria.atom.eventsourcing;

import io.memoria.atom.core.id.Id;

import java.util.UUID;

public class StateId extends Id {
  public StateId(String value) {
    super(value);
  }

  public StateId(long value) {
    super(value);
  }

  public StateId(UUID value) {
    super(value);
  }

  public StateId(Id id) {
    super(id);
  }

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
