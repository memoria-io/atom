package io.memoria.atom.reactive.eventsourcing.kafka;

import io.memoria.atom.core.eventsourcing.infra.stream.ESStreamMsg;
import io.vavr.collection.List;
import io.vavr.collection.Map;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.common.TopicPartition;

class KafkaUtils {
  private KafkaUtils() {}

  public static ESStreamMsg toMsg(ConsumerRecord<String, String> record) {
    return new ESStreamMsg(record.topic(), record.partition(), record.key(), record.value());
  }

  public static long topicSize(String topic, int partition, Map<String, Object> conf) {
    try (var consumer = new KafkaConsumer<Long, String>(conf.toJavaMap())) {
      var tp = new TopicPartition(topic, partition);
      var tpCol = List.of(tp).toJavaList();
      consumer.assign(tpCol);
      consumer.seekToEnd(tpCol);
      return consumer.position(tp);
    }
  }
}
