package io.memoria.atom.active.eventsourcing.kafka;

import io.memoria.atom.active.eventsourcing.kafka.infra.KafkaUtils;
import io.memoria.atom.active.eventsourcing.pipeline.CommandStream;
import io.memoria.atom.core.eventsourcing.Command;
import io.memoria.atom.core.text.TextTransformer;
import io.vavr.collection.Map;
import io.vavr.control.Try;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerRecord;

import java.time.Duration;
import java.util.stream.Stream;

public class KafkaCommandStream<C extends Command> implements CommandStream<C> {
  private final Duration pollDuration;
  private final KafkaProducer<String, String> producer;
  private final KafkaConsumer<String, String> consumer;
  private final TextTransformer transformer;
  private final Class<C> cClass;

  public KafkaCommandStream(Duration pollDuration,
                            Map<String, Object> producerConfig,
                            Map<String, Object> consumerConfig,
                            TextTransformer transformer,
                            Class<C> cClass) {
    this.pollDuration = pollDuration;
    this.producer = new KafkaProducer<>(producerConfig.toJavaMap());
    this.consumer = new KafkaConsumer<>(consumerConfig.toJavaMap());
    this.transformer = transformer;
    this.cClass = cClass;
  }

  @Override
  public Try<C> pub(String topic, int partition, C c) {
    return transformer.serialize(c)
                      .map(cStr -> toRecord(topic, partition, c.commandId().value(), cStr))
                      .flatMap(rec -> Try.of(() -> KafkaUtils.send(producer, rec)))
                      .map(msg -> c);
  }

  @Override
  public Stream<Try<C>> sub(String topic, int partition) {
    return KafkaUtils.stream(consumer, topic, partition, pollDuration).map(this::toMessage);
  }

  public ProducerRecord<String, String> toRecord(String topic, int partition, String key, String value) {
    return new ProducerRecord<>(topic, partition, key, value);
  }

  private Try<C> toMessage(ConsumerRecord<String, String> rec) {
    return transformer.deserialize(rec.value(), cClass);
  }
}
