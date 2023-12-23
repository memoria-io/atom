package io.memoria.atom.eventsourcing;

import io.memoria.atom.core.id.Id;
import io.memoria.atom.core.id.IdValue;

public class CommandId extends Id {
  public CommandId(IdValue idValue) {
    super(idValue);
  }
}
