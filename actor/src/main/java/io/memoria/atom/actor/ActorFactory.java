package io.memoria.atom.actor;

import io.memoria.atom.core.id.Id;

public interface ActorFactory {
  Actor create(Id id);
}
