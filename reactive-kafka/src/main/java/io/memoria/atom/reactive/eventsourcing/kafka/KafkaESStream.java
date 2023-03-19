package io.memoria.atom.reactive.eventsourcing.kafka;

import io.memoria.reactive.eventsourcing.infra.stream.ESStream;
import io.vavr.collection.Map;

import java.util.function.Supplier;

public interface KafkaESStream extends ESStream {
  static ESStream create(Map<String, Object> producerConfig,
                         Map<String, Object> consumerConfig,
                         Supplier<Long> timeSupplier) {
    return new DefaultKafkaESStream(producerConfig, consumerConfig, timeSupplier);
  }
}
