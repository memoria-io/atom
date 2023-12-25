package io.memoria.atom.eventsourcing;

import io.memoria.atom.core.id.Id;

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
