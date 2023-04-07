package io.memoria.atom.active.eventsourcing.infra.stream;

import io.memoria.atom.core.eventsourcing.Event;
import io.memoria.atom.core.eventsourcing.Shardable;
import io.memoria.atom.core.eventsourcing.StateId;
import io.memoria.atom.core.eventsourcing.infra.Topic;
import io.memoria.atom.core.eventsourcing.infra.stream.ESStreamMsg;
import io.memoria.atom.core.text.TextTransformer;
import io.vavr.control.Try;

import java.util.stream.Stream;

class EventStreamImpl<E extends Event> implements EventStream<E> {
  private final ESStream esStream;
  private final TextTransformer transformer;
  private final Class<E> cClass;
  private final Topic topic;

  EventStreamImpl(Topic topic, ESStream esStream, TextTransformer transformer, Class<E> cClass) {
    this.topic = topic;
    this.esStream = esStream;
    this.transformer = transformer;
    this.cClass = cClass;
  }

  public Try<E> pub(E e) {
    var partition = e.partition(topic.nPartitions());
    return transformer.serialize(e).flatMap(cStr -> pubMsg(topic.topic(), partition, e, cStr)).map(id -> e);
  }

  public Stream<Try<E>> sub(StateId stateId) {
    int partition = Shardable.partition(stateId, topic.nPartitions());
    return esStream.sub(topic.topic(), partition)
                   .filter(s -> s.key().equals(stateId.value()))
                   .map(msg -> transformer.deserialize(msg.value(), cClass));

  }

  private Try<ESStreamMsg> pubMsg(String topic, int partition, E e, String cStr) {
    return esStream.pub(new ESStreamMsg(topic, partition, e.stateId().value(), cStr));
  }
}
