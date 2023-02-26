package io.memoria.atom.active.eventsourcing.stream;

import io.memoria.atom.core.eventsourcing.Command;
import io.memoria.atom.core.text.TextTransformer;
import io.vavr.control.Try;

import java.util.stream.Stream;

class AbstractCommandStream<C extends Command> implements CommandStream<C> {
  private final ESStream esStream;
  private final TextTransformer transformer;
  private final Class<C> cClass;

  protected AbstractCommandStream(ESStream esStream, TextTransformer transformer, Class<C> cClass) {
    this.esStream = esStream;
    this.transformer = transformer;
    this.cClass = cClass;
  }

  public Try<C> pub(String topic, int partition, C c) {
    return transformer.serialize(c).flatMap(cStr -> pubMsg(topic, partition, c, cStr)).map(id -> c);
  }

  public Stream<Try<C>> sub(String topic, int partition) {
    return esStream.sub(topic, partition).map(msg -> transformer.deserialize(msg.value(), cClass));
  }

  private Try<ESStreamMsg> pubMsg(String topic, int partition, C c, String cStr) {
    return esStream.pub(new ESStreamMsg(topic, partition, c.commandId().value(), cStr));
  }
}
