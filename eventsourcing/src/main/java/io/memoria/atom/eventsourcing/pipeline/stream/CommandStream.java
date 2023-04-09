package io.memoria.atom.eventsourcing.pipeline.stream;

import io.memoria.atom.eventsourcing.Command;
import io.memoria.atom.eventsourcing.pipeline.CommandRoute;
import io.memoria.atom.core.text.TextTransformer;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface CommandStream<C extends Command> {
  Mono<C> pub(C c);

  Flux<C> sub();

  static <C extends Command> CommandStream<C> create(CommandRoute CommandRoute,
                                                     ESMsgStream ESMsgStream,
                                                     TextTransformer transformer,
                                                     Class<C> cClass) {
    return new CommandStreamImpl<>(CommandRoute, ESMsgStream, transformer, cClass);
  }
}
