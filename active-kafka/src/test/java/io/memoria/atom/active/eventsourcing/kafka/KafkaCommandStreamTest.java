package io.memoria.atom.active.eventsourcing.kafka;

import io.memoria.atom.core.eventsourcing.Command;
import io.memoria.atom.core.eventsourcing.CommandId;
import io.memoria.atom.core.eventsourcing.StateId;
import io.memoria.atom.core.text.SerializableTransformer;
import io.vavr.collection.List;
import io.vavr.control.Try;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.MethodOrderer.OrderAnnotation;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;

import java.time.Duration;
import java.util.Random;

@TestMethodOrder(OrderAnnotation.class)
class KafkaCommandStreamTest {
  private static final Random r = new Random();
  private static final String topic = "some_topic_" + r.nextInt();
  private static final KafkaCommandStream<GreetingCmd> client = createRepo();
  private static final int partition = 0;
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
    new Thread(() -> createMessages(msgCount, msgCount + 10).forEach(KafkaCommandStreamTest::delayedSend)).start();
    // Then
    client.stream()
          .map(Try::get)
          .takeWhile(cmd -> !cmd.message().equals("hello world:109"))
          .forEach(msg -> Assertions.assertTrue(msg.message().contains("hello world")));
  }

  private static List<GreetingCmd> createMessages(int start, int count) {
    return List.range(start, count)
               .map(i -> new GreetingCmd(CommandId.randomUUID(), StateId.of(0), "hello world:" + i));
  }

  private static void delayedSend(GreetingCmd msg) {
    try {
      Thread.sleep(100);
      client.push(msg);
    } catch (InterruptedException e) {
      throw new RuntimeException(e);
    }
  }

  private static KafkaCommandStream<GreetingCmd> createRepo() {
    return new KafkaCommandStream<>(topic,
                                    partition,
                                    1,
                                    GreetingCmd.class,
                                    new SerializableTransformer(),
                                    Duration.ofMillis(100),
                                    Dataset.producerConfigs(),
                                    Dataset.consumerConfigs());
  }

  private record GreetingCmd(CommandId commandId, StateId stateId, String message) implements Command {

    @Override
    public long timestamp() {
      return 0;
    }
  }
}
