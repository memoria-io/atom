package io.memoria.atom.eventsourcing;

import io.memoria.atom.core.id.Id;
import io.memoria.atom.core.id.IdValue;

public class StateId extends Id {
  public StateId(IdValue idValue) {
    super(idValue);
  }
}
