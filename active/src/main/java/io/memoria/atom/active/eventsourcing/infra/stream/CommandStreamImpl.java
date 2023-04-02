package io.memoria.atom.active.eventsourcing.infra.stream;

import io.memoria.atom.core.eventsourcing.infra.CRoute;
import io.memoria.atom.core.eventsourcing.infra.stream.ESStreamMsg;
import io.memoria.atom.core.eventsourcing.Command;
import io.memoria.atom.core.text.TextTransformer;
import io.vavr.control.Try;

import java.util.stream.Stream;

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

  public Try<C> pub(C c) {
    var partition = c.partition(CRoute.cmdTopicTotalPartitions());
    return transformer.serialize(c).flatMap(cStr -> pubMsg(CRoute.cmdTopic(), partition, c, cStr)).map(id -> c);
  }

  public Stream<Try<C>> sub() {
    return esStream.sub(CRoute.cmdTopic(), CRoute.cmdTopicSrcPartition())
                   .map(msg -> transformer.deserialize(msg.value(), cClass));

  }

  private Try<ESStreamMsg> pubMsg(String topic, int partition, C c, String cStr) {
    return esStream.pub(new ESStreamMsg(topic, partition, c.commandId().value(), cStr));
  }
}
