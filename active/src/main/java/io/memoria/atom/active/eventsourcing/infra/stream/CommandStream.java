package io.memoria.atom.active.eventsourcing.infra.stream;

import io.memoria.atom.core.eventsourcing.Route;
import io.memoria.atom.core.eventsourcing.Command;
import io.memoria.atom.core.text.TextTransformer;
import io.vavr.control.Try;

import java.util.stream.Stream;

public interface CommandStream<C extends Command> {
  Try<C> pub(C c);

  Stream<Try<C>> sub();

  static <C extends Command> CommandStream<C> create(Route route,
                                                     ESStream esStream,
                                                     TextTransformer transformer,
                                                     Class<C> cClass) {
    return new CommandStreamImpl<>(route, esStream, transformer, cClass);
  }
}
