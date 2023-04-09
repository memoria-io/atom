package io.memoria.atom.reactive.eventsourcing.nats;

import io.memoria.atom.core.stream.ESMsg;
import io.memoria.atom.core.stream.ESMsgStream;
import io.nats.client.JetStreamApiException;
import io.vavr.collection.HashSet;
import io.vavr.collection.List;
import org.junit.jupiter.api.MethodOrderer.OrderAnnotation;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import reactor.core.publisher.Flux;
import reactor.test.StepVerifier;

import java.io.IOException;
import java.util.Random;

@TestMethodOrder(OrderAnnotation.class)
class DefaultNatsESMsgStreamTest {
  private static final int MSG_COUNT = 1000;
  private static final Random r = new Random();
  private static final String topic = "topic" + r.nextInt(1000);
  private static final int partition = 1;
  private static final ESMsgStream repo;

  static {
    try {
      var streams = HashSet.of(TestUtils.streamConfig(topic, partition));
      var config = new Config("nats://localhost:4222", streams);
      repo = NatsESMsgStream.create(config);
    } catch (IOException | InterruptedException | JetStreamApiException e) {
      throw new IllegalArgumentException(e);
    }
  }

  @Test
  @Order(1)
  void t1_publish() throws InterruptedException {
    // Given
    var msgs = Flux.range(0, MSG_COUNT).map(i -> new ESMsg(topic, partition, i + "", "hello" + i));
    // When
    var pub = msgs.concatMap(repo::pub);
    // Then
    StepVerifier.create(pub).expectNextCount(MSG_COUNT).verifyComplete();
    Thread.sleep(100);
  }

  @Test
  @Order(2)
  void t2_subscribe() {
    // Given previous publish ran successfully
    var msgs = List.range(0, MSG_COUNT).map(i -> new ESMsg(topic, partition, i + "", "hello" + i));
    // When
    var sub = repo.sub(topic, partition).take(MSG_COUNT);
    // Given
    StepVerifier.create(sub).expectNextCount(MSG_COUNT).verifyComplete();
    StepVerifier.create(sub).expectNextSequence(msgs).verifyComplete();
  }
}
