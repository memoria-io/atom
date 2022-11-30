package io.memoria.atom.active.eventsourcing.pipeline;

import io.memoria.atom.core.eventsourcing.Command;
import io.memoria.atom.core.eventsourcing.State;
import io.vavr.control.Try;

import java.util.stream.Stream;

interface Pipeline<S extends State, C extends Command> {
  Try<Boolean> offer(C cmd);

  Stream<Try<S>> stream();
}
