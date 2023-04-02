package io.memoria.reactive.eventsourcing.infra.stream;

import io.memoria.atom.core.eventsourcing.Command;
import io.memoria.atom.core.eventsourcing.infra.CRoute;
import io.memoria.atom.core.eventsourcing.infra.stream.ESStreamMsg;
import io.memoria.atom.core.text.TextTransformer;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import static io.memoria.reactive.core.vavr.ReactorVavrUtils.toMono;

class CommandStreamImpl<C extends Command> implements CommandStream<C> {
  private final ESStream esStream;
  private final TextTransformer transformer;
  private final Class<C> cClass;
  private final CRoute CRoute;

  CommandStreamImpl(CRoute CRoute, ESStream esStream, TextTransformer transformer, Class<C> cClass) {
    this.CRoute = CRoute;
    this.esStream = esStream;
    this.transformer = transformer;
    this.cClass = cClass;
  }

  public Mono<C> pub(C c) {
    var partition = c.partition(CRoute.cmdTopicTotalPartitions());
    return toMono(transformer.serialize(c)).flatMap(cStr -> pubMsg(CRoute.cmdTopic(), partition, c, cStr)).map(id -> c);
  }

  public Flux<C> sub() {
    return esStream.sub(CRoute.cmdTopic(), CRoute.cmdTopicSrcPartition())
                   .flatMap(msg -> toMono(transformer.deserialize(msg.value(), cClass)));

  }

  private Mono<ESStreamMsg> pubMsg(String topic, int partition, C c, String cStr) {
    return esStream.pub(new ESStreamMsg(topic, partition, c.commandId().value(), cStr));
  }
}
