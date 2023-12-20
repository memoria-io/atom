package io.memoria.atom.actor;

import io.vavr.control.Try;

import java.util.function.Function;

public interface Actor extends Function<Message, Try<Message>> {
  ActorId actorId();
}
