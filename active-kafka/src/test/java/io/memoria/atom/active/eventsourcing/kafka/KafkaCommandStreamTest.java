package io.memoria.atom.active.eventsourcing.kafka;

import io.memoria.atom.core.eventsourcing.Command;
import io.memoria.atom.core.eventsourcing.CommandId;
import io.memoria.atom.core.eventsourcing.StateId;
import io.memoria.atom.core.text.SerializableTransformer;
import io.vavr.collection.List;
import io.vavr.control.Try;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.MethodOrderer.OrderAnnotation;

import java.time.Duration;
import java.util.Random;
import java.util.concurrent.atomic.AtomicLong;

@TestMethodOrder(OrderAnnotation.class)
class KafkaCommandStreamTest {
  private static final Random r = new Random();
  private static final String topic = "some_topic_" + r.nextInt();
  private static final KafkaCommandStream<UserCommand> client = createRepo();
  private static final int partition = 0;
  private static final int msgCount = 100;

  @Test
  @Order(1)
  void push() {
    // Given
    var msgs = createMessages(0, msgCount);
    // When
    var result = msgs.map(msg -> client.pub(topic, partition, msg));
    // Then
    result.forEach(tr -> Assertions.assertTrue(tr.isSuccess()));
  }

  @Test
  @Order(2)
  void stream() {
    // Given
    new Thread(() -> createMessages(msgCount, msgCount + 10).forEach(KafkaCommandStreamTest::delayedSend)).start();
    AtomicLong atomicLong = new AtomicLong();
    // Then
    client.sub(topic, partition)
          .map(Try::get)
          .takeWhile(cmd -> !cmd.commandId.value().equals("109"))
          .forEach(cmd -> Assertions.assertEquals(cmd.commandId, CommandId.of(atomicLong.getAndIncrement())));
  }

  private static List<UserCommand> createMessages(int start, int count) {
    return List.range(start, count).map(i -> new UserCommand(CommandId.of(i)));
  }

  private static void delayedSend(UserCommand msg) {
    try {
      Thread.sleep(100);
      client.pub(topic, partition, msg);
    } catch (InterruptedException e) {
      throw new RuntimeException(e);
    }
  }

  private static KafkaCommandStream<UserCommand> createRepo() {
    return new KafkaCommandStream<>(Duration.ofMillis(100),
                                    Dataset.producerConfigs(),
                                    Dataset.consumerConfigs(),
                                    new SerializableTransformer(),
                                    UserCommand.class);
  }

  record UserCommand(CommandId commandId) implements Command {

    @Override
    public long timestamp() {
      return 0;
    }

    @Override
    public StateId stateId() {
      return StateId.of(0);
    }
  }
}
