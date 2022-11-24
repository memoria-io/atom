package io.memoria.atom.active.kafka.infra;

import io.vavr.collection.List;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.clients.producer.RecordMetadata;
import org.apache.kafka.common.TopicPartition;

import java.time.Duration;
import java.util.concurrent.ExecutionException;
import java.util.function.Function;
import java.util.stream.Stream;

public class KafkaUtils {
  private KafkaUtils() {}

  public static Stream<ConsumerRecord<String, String>> stream(KafkaConsumer<String, String> consumer,
                                                              String topic,
                                                              int partition,
                                                              Duration duration) {
    var tp = new TopicPartition(topic, partition);
    var tpList = java.util.List.of(tp);
    consumer.assign(tpList);
    consumer.seekToBeginning(tpList);
    return Stream.generate(() -> consumer.poll(duration).records(tp).stream()).flatMap(Function.identity());
  }

  public static RecordMetadata send(KafkaProducer<String, String> producer, ProducerRecord<String, String> record)
          throws ExecutionException, InterruptedException {
    return producer.send(record).get();
  }

  public static long topicSize(KafkaConsumer<Long, String> consumer, String topic, int partition) {
    var tp = new TopicPartition(topic, partition);
    var tpCol = List.of(tp).toJavaList();
    consumer.assign(tpCol);
    consumer.seekToEnd(tpCol);
    return consumer.position(tp);
  }
}
