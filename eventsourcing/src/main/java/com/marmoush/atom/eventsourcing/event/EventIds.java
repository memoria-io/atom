package com.marmoush.atom.eventsourcing.event;

import com.marmoush.atom.core.id.Id;

import java.util.UUID;

public class EventIds {
  private EventIds() {}

  public static EventId of(String value) {
    return new EventId(value);
  }

  public static EventId of(long value) {
    return new EventId(value);
  }

  public static EventId of(UUID uuid) {
    return new EventId(uuid.toString());
  }

  public static EventId of(Id id) {
    return new EventId(id);
  }
}
