package io.memoria.atom.eventsourcing.stream;

import io.memoria.atom.core.stream.ESMsg;
import io.memoria.atom.core.stream.ESMsgStream;
import io.memoria.atom.core.text.TextTransformer;
import io.memoria.atom.core.vavr.ReactorVavrUtils;
import io.memoria.atom.eventsourcing.Command;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

class CommandStreamImpl<C extends Command> implements CommandStream<C> {
  private final ESMsgStream esMsgStream;
  private final TextTransformer transformer;
  private final Class<C> cClass;

  CommandStreamImpl(ESMsgStream esMsgStream, TextTransformer transformer, Class<C> cClass) {
    this.esMsgStream = esMsgStream;
    this.transformer = transformer;
    this.cClass = cClass;
  }

  public Mono<C> pub(String topic, int partition, C c) {
    return ReactorVavrUtils.tryToMono(() -> transformer.serialize(c))
                           .flatMap(cStr -> pubMsg(topic, partition, c, cStr))
                           .map(id -> c);
  }

  public Flux<C> sub(String topic, int partition) {
    return esMsgStream.sub(topic, partition)
                      .flatMap(msg -> ReactorVavrUtils.tryToMono(() -> transformer.deserialize(msg.value(), cClass)));

  }

  private Mono<ESMsg> pubMsg(String topic, int partition, C c, String cStr) {
    return esMsgStream.pub(new ESMsg(topic, partition, c.commandId().value(), cStr));
  }
}
