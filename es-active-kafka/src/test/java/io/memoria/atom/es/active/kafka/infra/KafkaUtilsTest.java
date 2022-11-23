package io.memoria.atom.es.active.kafka.infra;

import io.memoria.atom.es.active.kafka.Dataset;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

class KafkaUtilsTest {
  @Test
  void topicSize() {
    var consumer = new KafkaConsumer<Long, String>(Dataset.consumerConfigs().toJavaMap());
    var size = KafkaUtils.topicSize(consumer, "unknown_topic", 0);
    Assertions.assertEquals(0, size);
  }
}
