package io.memoria.atom.active.eventsourcing.kafka;

import io.memoria.atom.active.eventsourcing.stream.ESStreamMsg;
import io.memoria.atom.core.eventsourcing.CommandId;
import io.vavr.collection.List;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.MethodOrderer.OrderAnnotation;

import java.time.Duration;
import java.util.Random;
import java.util.concurrent.atomic.AtomicLong;

@TestMethodOrder(OrderAnnotation.class)
class KafkaESStreamTest {
  private static final Random r = new Random();
  private static final String topic = "some_topic_" + r.nextInt();
  private static final KafkaESStream client = createRepo();
  private static final int partition = 0;
  private static final int msgCount = 100;

  @Test
  @Order(1)
  void push() {
    // Given
    var msgs = createMessages(0, msgCount);
    // When
    var result = msgs.map(client::pub);
    // Then
    result.forEach(tr -> Assertions.assertTrue(tr.isSuccess()));
  }

  @Test
  @Order(2)
  void stream() {
    // Given
    new Thread(() -> createMessages(msgCount, msgCount + 10).forEach(KafkaESStreamTest::delayedSend)).start();
    AtomicLong atomicLong = new AtomicLong();
    // Then
    client.sub(topic, partition)
          .takeWhile(msg -> !msg.key().equals("109"))
          .forEach(msg -> Assertions.assertEquals(msg.key(), atomicLong.getAndIncrement() + ""));
  }

  private static List<ESStreamMsg> createMessages(int start, int count) {
    return List.range(start, count).map(i -> new ESStreamMsg(topic, partition, CommandId.of(i).value(), "hello"));
  }

  private static void delayedSend(ESStreamMsg esStreamMsg) {
    try {
      Thread.sleep(100);
      client.pub(esStreamMsg);
    } catch (InterruptedException e) {
      throw new RuntimeException(e);
    }
  }

  private static KafkaESStream createRepo() {
    return new KafkaESStream(Duration.ofMillis(100), Dataset.producerConfigs(), Dataset.consumerConfigs());
  }
}
