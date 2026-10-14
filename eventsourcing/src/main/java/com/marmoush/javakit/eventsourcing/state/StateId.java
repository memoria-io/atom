package com.marmoush.javakit.eventsourcing.state;

import com.marmoush.javakit.core.id.Id;

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
}
