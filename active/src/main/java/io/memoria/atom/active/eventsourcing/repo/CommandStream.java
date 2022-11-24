package io.memoria.atom.active.eventsourcing.repo;

import io.memoria.atom.core.eventsourcing.Command;
import io.vavr.control.Try;

import java.util.stream.Stream;

public interface CommandStream<C extends Command> {
  Stream<Try<C>> stream();

  Try<C> push(C cmd);
}
