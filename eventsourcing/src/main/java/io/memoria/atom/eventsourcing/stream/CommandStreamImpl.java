package io.memoria.atom.eventsourcing.stream;

import io.memoria.atom.core.stream.ESMsg;
import io.memoria.atom.core.stream.ESMsgStream;
import io.memoria.atom.core.text.TextTransformer;
import io.memoria.atom.core.vavr.ReactorVavrUtils;
import io.memoria.atom.eventsourcing.Command;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

class CommandStreamImpl<C extends Command> implements CommandStream<C> {
  private final String topic;
  private final int subPartition;
  private final int totalPubPartitions;
  private final ESMsgStream esMsgStream;
  private final TextTransformer transformer;
  private final Class<C> cClass;

  CommandStreamImpl(String topic,
                    int subPartition,
                    int totalPubPartitions,
                    ESMsgStream esMsgStream,
                    TextTransformer transformer,
                    Class<C> cClass) {
    this.topic = topic;
    this.subPartition = subPartition;
    this.totalPubPartitions = totalPubPartitions;
    this.esMsgStream = esMsgStream;
    this.transformer = transformer;
    this.cClass = cClass;
  }

  public Mono<C> pub(C c) {
    var partition = c.partition(totalPubPartitions);
    return ReactorVavrUtils.tryToMono(() -> transformer.serialize(c))
                           .flatMap(cStr -> pubMsg(topic, partition, c, cStr))
                           .map(id -> c);
  }

  public Flux<C> sub() {
    return esMsgStream.sub(topic, subPartition)
                      .flatMap(msg -> ReactorVavrUtils.tryToMono(() -> transformer.deserialize(msg.value(), cClass)));

  }

  private Mono<ESMsg> pubMsg(String topic, int partition, C c, String cStr) {
    return esMsgStream.pub(new ESMsg(topic, partition, c.commandId().value(), cStr));
  }
}
