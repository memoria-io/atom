package io.memoria.atom.active.eventsourcing.pipeline;

import io.memoria.atom.core.eventsourcing.Command;
import io.vavr.control.Try;

import java.util.stream.Stream;

public interface CommandStream<C extends Command> {
  Try<C> pub(String topic, int partition, C c);

  Stream<Try<C>> sub(String topic, int partition);
}
