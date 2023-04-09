package io.memoria.atom.eventsourcing.stream;

import io.memoria.atom.core.stream.ESMsg;
import io.memoria.atom.core.stream.ESMsgStream;
import io.memoria.atom.core.text.TextTransformer;
import io.memoria.atom.core.vavr.ReactorVavrUtils;
import io.memoria.atom.eventsourcing.Command;
import io.memoria.atom.eventsourcing.pipeline.CommandRoute;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import static io.memoria.atom.core.vavr.ReactorVavrUtils.toMono;

class CommandStreamImpl<C extends Command> implements CommandStream<C> {
  private final io.memoria.atom.core.stream.ESMsgStream ESMsgStream;
  private final TextTransformer transformer;
  private final Class<C> cClass;
  private final io.memoria.atom.eventsourcing.pipeline.CommandRoute CommandRoute;

  CommandStreamImpl(CommandRoute CommandRoute, ESMsgStream ESMsgStream, TextTransformer transformer, Class<C> cClass) {
    this.CommandRoute = CommandRoute;
    this.ESMsgStream = ESMsgStream;
    this.transformer = transformer;
    this.cClass = cClass;
  }

  public Mono<C> pub(C c) {
    var partition = c.partition(CommandRoute.cmdTopicPartitions());
    return toMono(transformer.serialize(c)).flatMap(cStr -> pubMsg(CommandRoute.cmdTopic(), partition, c, cStr))
                                           .map(id -> c);
  }

  public Flux<C> sub() {
    return ESMsgStream.sub(CommandRoute.cmdTopic(), CommandRoute.cmdTopicPartition())
                      .flatMap(msg -> ReactorVavrUtils.toMono(transformer.deserialize(msg.value(), cClass)));

  }

  private Mono<ESMsg> pubMsg(String topic, int partition, C c, String cStr) {
    return ESMsgStream.pub(new ESMsg(topic, partition, c.commandId().value(), cStr));
  }
}
