package io.memoria.atom.es.active.pipeline;

import io.memoria.atom.core.eventsourcing.Command;
import io.memoria.atom.core.eventsourcing.Event;
import io.memoria.atom.core.eventsourcing.State;
import io.vavr.control.Try;

import java.util.stream.Stream;

public interface Pipeline<S extends State, C extends Command, E extends Event> {
  Try<Boolean> offer(C cmd);

  Stream<Try<S>> stream();
}
