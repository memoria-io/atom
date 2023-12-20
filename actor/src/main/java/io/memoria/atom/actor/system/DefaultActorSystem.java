package io.memoria.atom.actor.system;

import io.memoria.atom.actor.Actor;
import io.memoria.atom.actor.ActorFactory;
import io.memoria.atom.actor.ActorId;
import io.memoria.atom.actor.Message;
import io.vavr.control.Try;

import java.io.IOException;
import java.util.Iterator;

class DefaultActorSystem implements ActorSystem {
  private final ActorStore actorStore;
  private final ActorFactory actorFactory;

  protected DefaultActorSystem(ActorStore actorStore, ActorFactory actorFactory) {
    this.actorStore = actorStore;
    this.actorFactory = actorFactory;
  }

  public Try<Message> apply(ActorId actorId, Message message) {
    actorStore.computeIfAbsent(actorId, actorFactory::create);
    return actorStore.get(actorId).apply(message);
  }

  @Override
  public void close() throws IOException {
    actorStore.close();
  }

  @Override
  public Iterator<Actor> iterator() {
    return actorStore.iterator();
  }

  @Override
  public ActorStore actorStore() {
    return actorStore;
  }

  @Override
  public ActorFactory actorFactory() {
    return actorFactory;
  }
}
