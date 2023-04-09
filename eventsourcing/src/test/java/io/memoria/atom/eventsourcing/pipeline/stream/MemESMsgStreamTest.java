package io.memoria.atom.eventsourcing.pipeline.stream;

import io.memoria.atom.eventsourcing.StateId;
import org.assertj.core.api.Assertions;
import org.awaitility.Awaitility;
import org.junit.jupiter.api.Test;
import reactor.core.publisher.Flux;
import reactor.test.StepVerifier;

import java.time.Duration;
import java.util.concurrent.atomic.AtomicInteger;

class MemESMsgStreamTest {
  private static final Duration timeout = Duration.ofSeconds(5);
  private static final int ELEMENTS_SIZE = 1000;
  private static final String topic = "some_topic";
  private static final StateId S0 = StateId.of(0);
  private static final StateId S1 = StateId.of(1);
  private static final int TOTAL_PARTITIONS = 2;

  private final ESMsgStream stream = new MemESMsgStream(topic, TOTAL_PARTITIONS);

  @Test
  void publishAndSubscribe() {
    // Given
    var msgs = createMessages(S0).concatWith(createMessages(S1));
    // When
    StepVerifier.create(msgs.flatMap(stream::pub)).expectNextCount(ELEMENTS_SIZE * 2).verifyComplete();
    // Then
    var latch0 = new AtomicInteger();
    stream.sub(topic, 0).take(ELEMENTS_SIZE).index().doOnNext(tup -> {
      Assertions.assertThat(tup.getT2().key()).isEqualTo(String.valueOf(tup.getT1().intValue()));
      Assertions.assertThat(tup.getT2().partition()).isEqualTo(0);
      latch0.incrementAndGet();
    }).subscribe();
    Awaitility.await().atMost(timeout).until(() -> latch0.get() == ELEMENTS_SIZE);

    // And
    var latch1 = new AtomicInteger();
    stream.sub(topic, 1).take(ELEMENTS_SIZE).index().doOnNext(tup -> {
      Assertions.assertThat(tup.getT2().key()).isEqualTo(String.valueOf(tup.getT1().intValue()));
      Assertions.assertThat(tup.getT2().partition()).isEqualTo(1);
      latch1.incrementAndGet();
    }).subscribe();
    Awaitility.await().atMost(timeout).until(() -> latch1.get() == ELEMENTS_SIZE);
  }

  private Flux<ESMsg> createMessages(StateId stateId) {
    return Flux.range(0, ELEMENTS_SIZE).map(i -> new ESMsg(topic, getPartition(stateId), String.valueOf(i), "hello"));
  }

  private static int getPartition(StateId stateId) {
    return Integer.parseInt(stateId.value()) % TOTAL_PARTITIONS;
  }
}
