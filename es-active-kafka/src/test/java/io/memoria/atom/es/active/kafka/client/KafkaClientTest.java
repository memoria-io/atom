package io.memoria.atom.es.active.kafka.client;

import io.memoria.atom.es.active.Dataset;
import io.vavr.collection.List;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.MethodOrderer.OrderAnnotation;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;

import java.time.Duration;
import java.util.Random;

@TestMethodOrder(OrderAnnotation.class)
class KafkaClientTest {
  private static final Random r = new Random();
  private static final String topic = "some_topic_" + r.nextInt();
  private static final int partition = 0;
  private static final KafkaClient client = createClient();
  private static final int msgCount = 100;

  @Test
  @Order(1)
  void push() {
    // Given
    var msgs = createMessages(0, msgCount);
    // When
    var result = msgs.map(client::push);
    // Then
    result.forEach(tr -> Assertions.assertTrue(tr.isSuccess()));
  }

  @Test
  @Order(2)
  void stream() {
    // Given
    new Thread(() -> createMessages(msgCount, msgCount + 10).forEach(KafkaClientTest::delayedSend)).start();
    // Then
    client.stream(topic, partition)
          .takeWhile(msg -> !msg.key().equals("key_109"))
          .forEach(msg -> Assertions.assertTrue(msg.key().contains("key_")));
  }

  private static List<Msg> createMessages(int start, int count) {
    return List.range(start, count).map(i -> new Msg(topic, partition, "key_" + i, "hello world" + i));
  }

  private static void delayedSend(Msg msg) {
    try {
      Thread.sleep(100);
      client.push(msg);
    } catch (InterruptedException e) {
      throw new RuntimeException(e);
    }
  }

  private static KafkaClient createClient() {
    return new KafkaClient(Dataset.producerConfigs(), Dataset.consumerConfigs(), Duration.ofMillis(100));
  }
}
