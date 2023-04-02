package io.memoria.atom.active.eventsourcing.infra.stream;

import io.memoria.atom.core.eventsourcing.Event;
import io.memoria.atom.core.eventsourcing.infra.CRoute;
import io.memoria.atom.core.eventsourcing.infra.QRoute;
import io.memoria.atom.core.eventsourcing.infra.stream.ESStreamMsg;
import io.memoria.atom.core.text.TextTransformer;
import io.vavr.control.Try;

import java.util.stream.Stream;

class EventStreamImpl<E extends Event> implements EventStream<E> {
  private final ESStream esStream;
  private final TextTransformer transformer;
  private final Class<E> cClass;
  private final QRoute qRoute;

  EventStreamImpl(CRoute qRoute, ESStream esStream, TextTransformer transformer, Class<E> cClass) {
    this.qRoute = qRoute;
    this.esStream = esStream;
    this.transformer = transformer;
    this.cClass = cClass;
  }

  public Try<E> pub(E e) {
    var partition = e.partition(qRoute.eventTopicTotalPartitions());
    return transformer.serialize(e).flatMap(cStr -> pubMsg(qRoute.eventTopic(), partition, e, cStr)).map(id -> e);
  }

  public Stream<Try<E>> sub() {
    return esStream.sub(qRoute.eventTopic(), qRoute.eventTopicPartition())
                   .map(msg -> transformer.deserialize(msg.value(), cClass));

  }

  private Try<ESStreamMsg> pubMsg(String topic, int partition, E e, String cStr) {
    return esStream.pub(new ESStreamMsg(topic, partition, e.stateId().value(), cStr));
  }
}
