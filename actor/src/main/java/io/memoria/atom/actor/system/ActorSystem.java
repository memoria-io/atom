package io.memoria.atom.actor.system;

import io.memoria.atom.actor.Actor;
import io.memoria.atom.actor.ActorFactory;
import io.memoria.atom.actor.ActorId;
import io.memoria.atom.actor.Message;
import io.vavr.control.Try;

import java.io.Closeable;
import java.util.function.BiFunction;

public interface ActorSystem extends Closeable, Iterable<Actor>, BiFunction<ActorId, Message, Try<Message>> {
  ActorStore actorStore();

  ActorFactory actorFactory();

  static ActorSystem create(ActorStore actorStore, ActorFactory actorFactory) {
    return new DefaultActorSystem(actorStore, actorFactory);
  }
}
