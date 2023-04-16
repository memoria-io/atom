package io.memoria.atom.reactive.eventsourcing.kafka;

import io.memoria.atom.core.stream.ESMsg;
import io.vavr.collection.List;
import io.vavr.collection.Map;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.common.TopicPartition;
import reactor.kafka.sender.KafkaSender;
import reactor.kafka.sender.SenderOptions;

class KafkaUtils {
  private KafkaUtils() {}

  public static ESMsg toMsg(ConsumerRecord<String, String> record) {
    return new ESMsg(record.topic(), record.partition(), record.key(), record.value());
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

  public static KafkaSender<String, String> createSender(Map<String, Object> config) {
    var senderOptions = SenderOptions.<String, String>create(config.toJavaMap());
    return KafkaSender.create(senderOptions);
  }
}
