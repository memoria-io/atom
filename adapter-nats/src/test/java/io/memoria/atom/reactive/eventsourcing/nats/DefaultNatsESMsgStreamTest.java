package io.memoria.atom.reactive.eventsourcing.nats;

import io.memoria.atom.core.stream.ESMsgStream;
import io.nats.client.JetStreamApiException;
import io.vavr.collection.List;
import org.junit.jupiter.api.Test;
import reactor.core.publisher.Flux;
import reactor.test.StepVerifier;

import java.io.IOException;
import java.util.Random;

class DefaultNatsESMsgStreamTest {
  private static final String natsUrl = "nats://localhost:4222";
  private static final int MSG_COUNT = 1000;
  private static final Random r = new Random();

  private final String topic = "topic" + r.nextInt(1000);
  private final int topicTotalPartitions = 1;
  private final ESMsgStream repo = createRepo(topic, topicTotalPartitions);

  @Test
  void publish() {
    // Given
    var partition = 0;
    var msgs = List.range(0, MSG_COUNT).map(i -> Tests.createEsMsg(topic, partition, i));
    // When
    var pub = Flux.fromIterable(msgs).concatMap(repo::pub);
    // Then
    StepVerifier.create(pub).expectNextCount(MSG_COUNT).verifyComplete();
  }

  @Test
  void subscribe() {
    // Given
    var partition = 0;
    var msgs = List.range(0, MSG_COUNT).map(i -> Tests.createEsMsg(topic, partition, i));
    var pub = Flux.fromIterable(msgs).concatMap(repo::pub);

    // When
    var sub = repo.sub(topic, partition).take(MSG_COUNT);

    // Given
    StepVerifier.create(pub).expectNextCount(MSG_COUNT).verifyComplete();
    StepVerifier.create(sub).expectNextCount(MSG_COUNT).verifyComplete();
    StepVerifier.create(sub).expectNextSequence(msgs).verifyComplete();
  }

  private ESMsgStream createRepo(String topic, int nTotalPartitions) {
    var natsConfig = new NatsConfig(natsUrl, Tests.createConfigs(topic, nTotalPartitions));
    try {
      return NatsESMsgStream.create(natsConfig);
    } catch (IOException | InterruptedException | JetStreamApiException e) {
      throw new RuntimeException(e);
    }
  }
}
