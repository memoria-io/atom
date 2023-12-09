package io.memoria.atom.actor;

import io.memoria.atom.core.Identifiable;
import io.memoria.atom.core.Shardable;
import io.vavr.control.Try;

import java.util.function.Function;

public interface Actor extends Function<Message, Try<Message>>, Identifiable, Shardable {

}
