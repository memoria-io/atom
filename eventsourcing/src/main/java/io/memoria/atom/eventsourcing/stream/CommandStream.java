package io.memoria.atom.eventsourcing.stream;

import io.memoria.atom.core.stream.ESMsgStream;
import io.memoria.atom.core.text.TextTransformer;
import io.memoria.atom.eventsourcing.Command;
import io.memoria.atom.eventsourcing.pipeline.CommandRoute;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface CommandStream<C extends Command> {
  Mono<C> pub(C c);

  Flux<C> sub();

  static <C extends Command> CommandStream<C> create(CommandRoute commandRoute,
                                                     ESMsgStream esMsgStream,
                                                     TextTransformer transformer,
                                                     Class<C> cClass) {
    return new CommandStreamImpl<>(commandRoute, esMsgStream, transformer, cClass);
  }
}
