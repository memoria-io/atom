package io.memoria.atom.actor;

public abstract class AbstractActor implements Actor {
  private final ActorId actorId;

  protected AbstractActor(ActorId actorId) {
    this.actorId = actorId;
  }

  @Override
  public ActorId actorId() {
    return this.actorId;
  }
}
