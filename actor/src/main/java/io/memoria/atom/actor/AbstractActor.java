package io.memoria.atom.actor;

import io.memoria.atom.core.id.Id;

public abstract class AbstractActor implements Actor {
  private final Id id;

  protected AbstractActor(Id id) {
    this.id = id;
  }

  @Override
  public Id id() {
    return id;
  }
}
