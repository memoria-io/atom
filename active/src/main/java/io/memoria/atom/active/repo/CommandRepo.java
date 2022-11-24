package io.memoria.atom.active.repo;

import io.memoria.atom.core.eventsourcing.Command;
import io.vavr.control.Try;

import java.util.stream.Stream;

public interface CommandRepo<C extends Command> {
  Stream<Try<C>> stream();

  Try<C> push(C cmd);
}
