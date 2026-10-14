package com.marmoush.javakit.eventsourcing.event;

import com.marmoush.javakit.core.id.Id;

import java.util.UUID;

public class EventId extends Id {

  public EventId(String value) {
    super(value);
  }

  public EventId(long value) {
    super(value);
  }

  public EventId(UUID value) {
    super(value);
  }

  public EventId(Id id) {
    super(id);
  }
}
