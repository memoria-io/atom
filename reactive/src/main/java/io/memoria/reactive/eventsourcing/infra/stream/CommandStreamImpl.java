package io.memoria.reactive.eventsourcing.infra.stream;

import io.memoria.atom.core.eventsourcing.Command;
import io.memoria.atom.core.eventsourcing.Route;
import io.memoria.atom.core.eventsourcing.infra.stream.ESStreamMsg;
import io.memoria.atom.core.text.TextTransformer;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import static io.memoria.reactive.core.vavr.ReactorVavrUtils.toMono;

class CommandStreamImpl<C extends Command> implements CommandStream<C> {
  private final ESStream esStream;
  private final TextTransformer transformer;
  private final Class<C> cClass;
  private final Route route;

  CommandStreamImpl(Route route, ESStream esStream, TextTransformer transformer, Class<C> cClass) {
    this.route = route;
    this.esStream = esStream;
    this.transformer = transformer;
    this.cClass = cClass;
  }

  public Mono<C> pub(C c) {
    var partition = c.partition(route.totalCmdPartitions());
    return toMono(transformer.serialize(c)).flatMap(cStr -> pubMsg(route.cmdTopic(), partition, c, cStr)).map(id -> c);
  }

  public Flux<C> sub() {
    return esStream.sub(route.cmdTopic(), route.cmdPartition())
                   .flatMap(msg -> toMono(transformer.deserialize(msg.value(), cClass)));

  }

  private Mono<ESStreamMsg> pubMsg(String topic, int partition, C c, String cStr) {
    return esStream.pub(new ESStreamMsg(topic, partition, c.commandId().value(), cStr));
  }
}
