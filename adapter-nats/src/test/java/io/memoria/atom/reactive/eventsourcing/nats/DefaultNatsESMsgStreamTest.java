package io.memoria.atom.reactive.eventsourcing.nats;

import io.memoria.atom.core.stream.ESMsg;
import io.memoria.atom.core.stream.ESMsgStream;
import io.nats.client.JetStreamApiException;
import io.vavr.collection.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import reactor.core.publisher.Flux;
import reactor.test.StepVerifier;

import java.io.IOException;
import java.time.Duration;
import java.util.Random;

class DefaultNatsESMsgStreamTest {
  private static final String natsUrl = "nats://localhost:4222";
  private static final int MSG_COUNT = 1000;
  private static final Random r = new Random();

  private final String topic = "topic" + r.nextInt(1000);
  private final int partition = 1;
  private final ESMsgStream repo = createRepo();

  @Test
  void publish() {
    // Given
    var msgs = Flux.range(0, MSG_COUNT).map(this::createEsMsg);
    // When
    var pub = msgs.concatMap(repo::pub);
    // Then
    StepVerifier.create(pub).expectNextCount(MSG_COUNT).verifyComplete();
  }

  @Test
  void subscribe() {
    // Given
    var msgs = List.range(0, MSG_COUNT).map(this::createEsMsg);

    // When
    var sub = repo.sub(topic, partition).take(MSG_COUNT);

    // Given
    StepVerifier.create(sub).expectNextCount(MSG_COUNT).verifyComplete();
    StepVerifier.create(sub).expectNextSequence(msgs).verifyComplete();
  }

  @Test
  @DisplayName("Subscribe to last message of a non empty topic")
  void subscribeToLast() {
    // Given
    var msgs = List.range(0, MSG_COUNT).map(this::createEsMsg);
    // When
    Thread.startVirtualThread(() -> {
      var m = Flux.fromIterable(msgs).concatMap(repo::pub);
      StepVerifier.create(m).expectNextCount(msgs.size()).verifyComplete();
    });
    var sub = repo.getLast(topic, partition, Duration.ofMillis(1000));

    // Then
    StepVerifier.create(sub).expectNext(msgs.last()).verifyComplete();
  }

  @Test
  @DisplayName("Subscribe to last message of non existing topic")
  void subscribeToLastOfNonExisting() {
    // Given
    /* Silence is golden */

    // When
    var sub = repo.getLast(topic, partition, Duration.ofMillis(500));

    // Then
    StepVerifier.create(sub).expectComplete();
  }

  private ESMsg createEsMsg(Integer i) {
    return new ESMsg(topic, partition, String.valueOf(i), "hello" + i);
  }

  private ESMsgStream createRepo() {
    var natsConfig = new NatsConfig(natsUrl, Tests.topicConfig(topic, partition));
    try {
      return NatsESMsgStream.create(natsConfig);
    } catch (IOException | InterruptedException | JetStreamApiException e) {
      throw new RuntimeException(e);
    }
  }
}
