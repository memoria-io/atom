package io.memoria.atom.es.active.kafka.client;

import io.vavr.collection.Map;
import io.vavr.control.Try;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.clients.producer.KafkaProducer;
import io.memoria.atom.es.active.kafka.infra.KafkaUtils;
import java.time.Duration;
import java.util.stream.Stream;

public class KafkaClient {
  private final Duration pollDuration;
  private final KafkaProducer<String, String> producer;
  private final KafkaConsumer<String, String> consumer;

  public KafkaClient(Map<String, Object> producerConfig, Map<String, Object> consumerConfig, Duration pollDuration) {
    this.pollDuration = pollDuration;
    this.producer = new KafkaProducer<>(producerConfig.toJavaMap());
    this.consumer = new KafkaConsumer<>(consumerConfig.toJavaMap());
  }

  public Try<Msg> push(Msg msg) {
    return Try.of(() -> KafkaUtils.send(producer, msg.toRecord())).map(meta -> msg);
  }

  public Stream<Msg> stream(String topic, int partition) {
    return KafkaUtils.stream(consumer, topic, partition, pollDuration).map(Msg::from);
  }
}
