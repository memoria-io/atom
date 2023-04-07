package io.memoria.atom.reactive.eventsourcing.kafka;

import io.memoria.atom.core.eventsourcing.pipeline.stream.ESMsg;
import io.memoria.atom.core.eventsourcing.pipeline.stream.ESMsgStream;
import org.junit.jupiter.api.MethodOrderer.OrderAnnotation;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import reactor.core.publisher.Flux;
import reactor.test.StepVerifier;

import java.util.Random;

@TestMethodOrder(OrderAnnotation.class)
class DefaultKafkaESMsgStreamTest {
  private static final Random random = new Random();
  private static final int MSG_COUNT = 1000;
  private static final String TOPIC = "node" + random.nextInt(1000);
  private static final int PARTITION = 0;
  private static final ESMsgStream repo;

  static {
    repo = KafkaESMsgStream.create(Dataset.producerConfigs(), Dataset.consumerConfigs(), () -> 1L);
  }

  @Test
  @Order(1)
  void publish() {
    // Given
    var msgs = Flux.range(0, MSG_COUNT).map(i -> new ESMsg(TOPIC, PARTITION, i + "", "hello" + i));
    // When
    var pub = msgs.concatMap(repo::pub);
    // Then
    StepVerifier.create(pub).expectNextCount(MSG_COUNT).verifyComplete();
  }

  @Test
  @Order(2)
  void subscribe() {
    // Given previous publish ran successfully
    // When
    var sub = repo.sub(TOPIC, PARTITION).take(MSG_COUNT);
    // Given
    StepVerifier.create(sub).expectNextCount(MSG_COUNT).verifyComplete();
  }
}
