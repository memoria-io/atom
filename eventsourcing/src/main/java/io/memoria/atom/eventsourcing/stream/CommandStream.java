package io.memoria.atom.eventsourcing.stream;

import io.memoria.atom.core.stream.ESMsgStream;
import io.memoria.atom.core.text.TextTransformer;
import io.memoria.atom.eventsourcing.Command;
import io.memoria.atom.eventsourcing.pipeline.PipelineRoute;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface CommandStream<C extends Command> {
  Mono<C> pub(C c);

  Flux<C> sub();

  static <C extends Command> CommandStream<C> create(PipelineRoute route,
                                                     ESMsgStream esMsgStream,
                                                     TextTransformer transformer,
                                                     Class<C> cClass) {
    return new CommandStreamImpl<>(route.cmdTopic(),
                                   route.cmdSubPartition(),
                                   route.cmdTotalPubPartitions(),
                                   esMsgStream,
                                   transformer,
                                   cClass);
  }

  static <C extends Command> CommandStream<C> create(String topic,
                                                     int subPartition,
                                                     int totalPubPartitions,
                                                     ESMsgStream esMsgStream,
                                                     TextTransformer transformer,
                                                     Class<C> cClass) {
    return new CommandStreamImpl<>(topic, subPartition, totalPubPartitions, esMsgStream, transformer, cClass);
  }
}
