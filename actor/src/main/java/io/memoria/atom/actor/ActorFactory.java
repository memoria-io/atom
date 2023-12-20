package io.memoria.atom.actor;

public interface ActorFactory {
  Actor create(ActorId id);
}
