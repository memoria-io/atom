package io.memoria.reactive.eventsourcing.mem;

import io.memoria.atom.core.id.Id;
import io.memoria.reactive.eventsourcing.repo.Msg;
import io.memoria.reactive.eventsourcing.repo.Stream;
import io.memoria.reactive.eventsourcing.repo.StreamConfig;
import io.memoria.reactive.eventsourcing.repo.mem.MemStream;
import org.junit.jupiter.api.MethodOrderer.OrderAnnotation;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import reactor.core.publisher.Flux;
import reactor.test.StepVerifier;

import java.time.Duration;
import java.util.Objects;

@TestMethodOrder(OrderAnnotation.class)
class MemStreamTest {
  private static final int ELEMENTS_SIZE = 1000;
  private static final int DOUBLE_ELEMENTS = 2 * ELEMENTS_SIZE;
  private static final String topic = "NODE_TOPIC";
  private static final int PARTITION = 0;
  private static final Stream stream = new MemStream(new StreamConfig(topic, 1, Integer.MAX_VALUE));

  @Test
  @Order(0)
  void publish() {
    // Given
    var msgs = createMsgs();
    // When
    var pub = stream.publish(msgs).map(Msg::id);
    // Then
    var expected = Objects.requireNonNull(msgs.map(Msg::id).collectList().block());
    StepVerifier.create(pub).expectNextSequence(expected).verifyComplete();
  }

  @Test
  @Order(1)
  void subscribe() {
    // Given
    var msgs = createMsgs();
    stream.publish(msgs).subscribe();
    // When
    var sub = stream.subscribe(topic, PARTITION, 0).map(Msg::id).take(DOUBLE_ELEMENTS);
    // Then
    StepVerifier.create(sub).expectNextCount(DOUBLE_ELEMENTS).verifyComplete();
    // And resubscribing works   
    StepVerifier.create(sub).expectNextCount(DOUBLE_ELEMENTS).verifyComplete();
  }

  @Test
  @Order(2)
  void delayedSubscribe() {
    // When
    var sub = stream.subscribe(topic, PARTITION, 0).delaySubscription(Duration.ofMillis(1000)).take(DOUBLE_ELEMENTS);
    // Then
    StepVerifier.create(sub).expectNextCount(DOUBLE_ELEMENTS).verifyComplete();
  }

  @Test
  @Order(3)
  void size() {
    StepVerifier.create(stream.size(topic, PARTITION)).expectNext((long) DOUBLE_ELEMENTS).verifyComplete();
  }

  private Flux<Msg> createMsgs() {
    return Flux.range(0, ELEMENTS_SIZE).map(i -> new Msg(topic, PARTITION, Id.of(i), "hello" + i));
  }
}
