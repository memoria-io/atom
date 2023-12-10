package io.memoria.atom.actor.system;

import io.memoria.atom.actor.ActorId;
import io.memoria.atom.actor.Message;
import io.vavr.control.Try;

public interface ActorSystem {
  Try<Message> handle(ActorId actorId, Message message);
}
