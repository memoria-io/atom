package io.memoria.atom.active.eventsourcing.adapter.stream;

import io.memoria.atom.core.eventsourcing.Route;
import io.memoria.atom.active.eventsourcing.infra.stream.ESStream;
import io.memoria.atom.core.eventsourcing.infra.stream.ESStreamMsg;
import io.memoria.atom.core.eventsourcing.Command;
import io.memoria.atom.core.text.TextTransformer;
import io.vavr.control.Try;

import java.util.stream.Stream;

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

  public Try<C> pub(C c) {
    var partition = c.partition(route.totalCmdPartitions());
    return transformer.serialize(c).flatMap(cStr -> pubMsg(route.cmdTopic(), partition, c, cStr)).map(id -> c);
  }

  public Stream<Try<C>> sub() {
    return esStream.sub(route.cmdTopic(), route.cmdPartition())
                   .map(msg -> transformer.deserialize(msg.value(), cClass));

  }

  private Try<ESStreamMsg> pubMsg(String topic, int partition, C c, String cStr) {
    return esStream.pub(new ESStreamMsg(topic, partition, c.commandId().value(), cStr));
  }
}
