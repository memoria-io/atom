package io.memoria.atom.es.active.kafka.client;

import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.producer.ProducerRecord;

public record Msg(String topic, int partition, String key, String body) {

  public Msg {
    if (partition < 0) {
      throw new IllegalArgumentException("Partition value can't be less than zero");
    }
  }

  public ProducerRecord<String, String> toRecord() {
    return new ProducerRecord<>(topic, partition, key, body);
  }

  public static Msg from(ConsumerRecord<String, String> record) {
    return new Msg(record.topic(), record.partition(), record.key(), record.value());
  }
}
