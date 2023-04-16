package io.memoria.atom.reactive.eventsourcing.kafka;

import io.memoria.atom.core.stream.ESMsgStream;
import io.vavr.collection.Map;
import reactor.kafka.sender.KafkaSender;

import java.util.function.Supplier;

public interface KafkaESMsgStream extends ESMsgStream {
  static ESMsgStream create(KafkaSender<String, String> sender,
                            Map<String, Object> producerConfig,
                            Map<String, Object> consumerConfig,
                            Supplier<Long> timeSupplier) {
    return new DefaultKafkaESMsgStream(sender, producerConfig, consumerConfig, timeSupplier);
  }
}
