package io.memoria.atom.eventsourcing.pipeline.stream;

import io.memoria.atom.eventsourcing.Event;
import io.memoria.atom.core.text.TextTransformer;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import static io.memoria.atom.core.vavr.ReactorVavrUtils.toMono;

class EventStreamImpl<E extends Event> implements EventStream<E> {
  private final ESMsgStream ESMsgStream;
  private final TextTransformer transformer;
  private final Class<E> cClass;
  private final String topic;
  private final int partition;

  EventStreamImpl(String topic, int partition, ESMsgStream ESMsgStream, TextTransformer transformer, Class<E> cClass) {
    this.topic = topic;
    this.partition = partition;
    this.ESMsgStream = ESMsgStream;
    this.transformer = transformer;
    this.cClass = cClass;
  }

  public Mono<E> pub(E e) {
    return toMono(transformer.serialize(e)).flatMap(cStr -> pubMsg(topic, partition, e, cStr)).map(id -> e);
  }

  public Flux<E> sub() {
    return ESMsgStream.sub(topic, partition)
                      .flatMap(msg -> toMono(transformer.deserialize(msg.value(), cClass)));

  }

  private Mono<ESMsg> pubMsg(String topic, int partition, E e, String cStr) {
    return ESMsgStream.pub(new ESMsg(topic, partition, e.commandId().value(), cStr));
  }
}
