package io.memoria.atom.active.eventsourcing.kafka;

import io.memoria.atom.active.eventsourcing.kafka.infra.KafkaUtils;
import io.memoria.atom.active.eventsourcing.infra.stream.ESStream;
import io.memoria.atom.core.eventsourcing.infra.stream.ESStreamMsg;
import io.vavr.collection.Map;
import io.vavr.control.Try;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerRecord;

import java.time.Duration;
import java.util.stream.Stream;

public class KafkaESStream implements ESStream {
  private final Duration pollDuration;
  private final KafkaProducer<String, String> producer;
  private final KafkaConsumer<String, String> consumer;

  public KafkaESStream(Duration pollDuration, Map<String, Object> producerConfig, Map<String, Object> consumerConfig) {
    this.pollDuration = pollDuration;
    this.producer = new KafkaProducer<>(producerConfig.toJavaMap());
    this.consumer = new KafkaConsumer<>(consumerConfig.toJavaMap());
  }

  @Override
  public Try<ESStreamMsg> pub(ESStreamMsg esStreamMsg) {
    var prodRec = toRecord(esStreamMsg.topic(), esStreamMsg.partition(), esStreamMsg.key(), esStreamMsg.value());
    return Try.of(() -> KafkaUtils.send(producer, prodRec)).map(msg -> esStreamMsg);
  }

  @Override
  public Stream<ESStreamMsg> sub(String topic, int partition) {
    return KafkaUtils.stream(consumer, topic, partition, pollDuration).map(this::toMsg);
  }

  public ProducerRecord<String, String> toRecord(String topic, int partition, String key, String value) {
    return new ProducerRecord<>(topic, partition, key, value);
  }

  private ESStreamMsg toMsg(ConsumerRecord<String, String> rec) {
    return new ESStreamMsg(rec.topic(), rec.partition(), rec.key(), rec.value());
  }
}
