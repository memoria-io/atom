package com.marmoush.atom.eventsourcing.state;

import com.marmoush.atom.core.id.Id;

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
