package io.memoria.atom.es.active.kafka;

import io.memoria.atom.core.eventsourcing.Command;
import io.memoria.atom.core.text.TextTransformer;
import io.memoria.atom.es.active.kafka.infra.KafkaUtils;
import io.memoria.atom.es.active.repo.CommandRepo;
import io.vavr.collection.Map;
import io.vavr.control.Try;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerRecord;

import java.time.Duration;
import java.util.stream.Stream;

public class KafkaCommandRepo<C extends Command> implements CommandRepo<C> {
  private final String topic;
  private final int streamPartition;
  private final int totalPartitions;

  private final Class<C> cClass;
  private final TextTransformer transformer;
  private final Duration pollDuration;
  private final KafkaProducer<String, String> producer;
  private final KafkaConsumer<String, String> consumer;

  public KafkaCommandRepo(String topic,
                          int partition,
                          int totalPartitions,
                          Class<C> cClass,
                          TextTransformer transformer,
                          Duration pollDuration,
                          Map<String, Object> producerConfig,
                          Map<String, Object> consumerConfig) {
    this.topic = topic;
    this.streamPartition = partition;
    this.totalPartitions = totalPartitions;
    this.cClass = cClass;
    this.transformer = transformer;
    this.pollDuration = pollDuration;
    this.producer = new KafkaProducer<>(producerConfig.toJavaMap());
    this.consumer = new KafkaConsumer<>(consumerConfig.toJavaMap());
  }

  @Override
  public Stream<Try<C>> stream() {
    return KafkaUtils.stream(consumer, topic, streamPartition, pollDuration).map(this::toCommand);
  }

  @Override
  public Try<C> push(C cmd) {
    return toRecord(cmd).flatMap(c -> Try.of(() -> KafkaUtils.send(producer, c))).map(meta -> cmd);
  }

  public Try<ProducerRecord<String, String>> toRecord(C cmd) {
    var partition = cmd.partition(totalPartitions);
    var key = cmd.commandId().value();
    return transformer.serialize(cmd).map(b -> new ProducerRecord<>(topic, partition, key, b));
  }

  private Try<C> toCommand(ConsumerRecord<String, String> msg) {
    return transformer.deserialize(msg.value(), cClass);
  }
}
