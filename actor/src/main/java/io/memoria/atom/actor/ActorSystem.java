package io.memoria.atom.actor;

import io.vavr.control.Try;

public interface ActorSystem {
  Try<Message> handle(ActorId actorId, Message message);
}
