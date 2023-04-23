package io.memoria.atom.eventsourcing.stream;

import io.memoria.atom.core.stream.ESMsgStream;
import io.memoria.atom.core.text.TextTransformer;
import io.memoria.atom.eventsourcing.Command;
import io.memoria.atom.eventsourcing.pipeline.PipelineRoute;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface CommandStream<C extends Command> {
  Mono<C> pub(String topic, int partition, C c);

  Flux<C> sub(String topic, int partition);

  static <C extends Command> CommandStream<C> create(ESMsgStream esMsgStream,
                                                     TextTransformer transformer,
                                                     Class<C> cClass) {
    return new CommandStreamImpl<>(esMsgStream, transformer, cClass);
  }
}
