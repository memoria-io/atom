package io.memoria.atom.active.eventsourcing.kafka;

import io.memoria.atom.active.eventsourcing.kafka.infra.KafkaUtils;
import io.memoria.atom.active.eventsourcing.repo.CmdMsg;
import io.memoria.atom.active.eventsourcing.repo.CommandStream;
import io.vavr.collection.Map;
import io.vavr.control.Try;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerRecord;

import java.time.Duration;
import java.util.stream.Stream;

public class KafkaCommandStream implements CommandStream {
  private final Duration pollDuration;
  private final KafkaProducer<String, String> producer;
  private final KafkaConsumer<String, String> consumer;

  public KafkaCommandStream(Duration pollDuration,
                            Map<String, Object> producerConfig,
                            Map<String, Object> consumerConfig) {
    this.pollDuration = pollDuration;
    this.producer = new KafkaProducer<>(producerConfig.toJavaMap());
    this.consumer = new KafkaConsumer<>(consumerConfig.toJavaMap());
  }

  @Override
  public Try<CmdMsg> pub(CmdMsg cmd) {
    var rec = toRecord(cmd);
    return Try.of(() -> KafkaUtils.send(producer, rec)).map(meta -> cmd);
  }

  @Override
  public Stream<CmdMsg> sub(String topic, int partition) {
    return KafkaUtils.stream(consumer, topic, partition, pollDuration).map(KafkaCommandStream::toMessage);
  }

  public ProducerRecord<String, String> toRecord(CmdMsg cmdMsg) {
    return new ProducerRecord<>(cmdMsg.topic(), cmdMsg.partition(), cmdMsg.key(), cmdMsg.value());
  }

  private static CmdMsg toMessage(ConsumerRecord<String, String> rec) {
    return CmdMsg.create(rec.topic(), rec.partition(), rec.key(), rec.value());
  }
}
