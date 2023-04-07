package io.memoria.atom.reactive.eventsourcing.kafka;

import io.memoria.atom.core.eventsourcing.pipeline.stream.ESMsgStream;
import io.vavr.collection.Map;

import java.util.function.Supplier;

public interface KafkaESMsgStream extends ESMsgStream {
  static ESMsgStream create(Map<String, Object> producerConfig,
                            Map<String, Object> consumerConfig,
                            Supplier<Long> timeSupplier) {
    return new DefaultKafkaESMsgStream(producerConfig, consumerConfig, timeSupplier);
  }
}
