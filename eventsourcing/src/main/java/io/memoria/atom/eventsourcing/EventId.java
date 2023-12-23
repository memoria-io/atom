package io.memoria.atom.eventsourcing;

import io.memoria.atom.core.id.Id;
import io.memoria.atom.core.id.IdValue;

public class EventId extends Id {

  public EventId(IdValue idValue) {
    super(idValue);
  }
}
