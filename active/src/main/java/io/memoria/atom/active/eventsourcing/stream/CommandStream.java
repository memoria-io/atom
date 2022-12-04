package io.memoria.atom.active.eventsourcing.stream;

import io.memoria.atom.core.eventsourcing.Command;
import io.memoria.atom.core.text.TextTransformer;
import io.vavr.control.Try;

import java.util.Map;
import java.util.stream.Stream;

public interface CommandStream<C extends Command> {
  Try<C> pub(String topic, int partition, C c);

  Stream<Try<C>> sub(String topic, int partition);

  static <C extends Command> CommandStream<C> from(ESStream esStream, TextTransformer transformer, Class<C> cClass) {
    return new AbstractCommandStream<>(esStream, transformer, cClass);
  }

  static <C extends Command> CommandStream<C> fromMemory(Map<String, Integer> m) {
    return new MemCommandStream<>(m);
  }

  static <C extends Command> CommandStream<C> fromMemory(String topic, int totalPartitions) {
    return new MemCommandStream<>(topic, totalPartitions);
  }
}
