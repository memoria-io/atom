package io.memoria.reactive.eventsourcing.infra.stream;

import io.memoria.atom.core.eventsourcing.Command;
import io.memoria.atom.core.eventsourcing.infra.CRoute;
import io.memoria.atom.core.text.TextTransformer;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface CommandStream<C extends Command> {
  Mono<C> pub(C c);

  Flux<C> sub();

  static <C extends Command> CommandStream<C> create(CRoute CRoute,
                                                     ESStream esStream,
                                                     TextTransformer transformer,
                                                     Class<C> cClass) {
    return new CommandStreamImpl<>(CRoute, esStream, transformer, cClass);
  }
}
