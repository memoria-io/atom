package io.memoria.atom.eventsourcing.stream;

import io.memoria.atom.core.id.Id;
import io.memoria.atom.core.stream.ESMsgStream;
import io.memoria.atom.core.text.SerializableTransformer;
import io.memoria.atom.eventsourcing.Command;
import io.memoria.atom.eventsourcing.pipeline.CommandRoute;
import org.assertj.core.api.Assertions;
import org.awaitility.Awaitility;
import org.junit.jupiter.api.Test;
import reactor.core.publisher.Flux;
import reactor.test.StepVerifier;

import java.time.Duration;
import java.util.concurrent.atomic.AtomicInteger;

class CommandStreamImplTest {
  private static final Duration timeout = Duration.ofSeconds(5);

  private static final int ELEMENTS_SIZE = 1000;
  private static final Id S0 = Id.of(0);
  private static final Id S1 = Id.of(1);

  @Test
  void publishAndSubscribe() {
    // Given
    var route = createRoute(0);
    var esStream = ESMsgStream.inMemory(route.totalPartitions(), route.cmdTopic());
    var stream = CommandStream.create(route, esStream, new SerializableTransformer(), SomeCommand.class);
    var msgs = createMessages(S0).concatWith(createMessages(S1));

    // When
    StepVerifier.create(msgs.flatMap(stream::pub)).expectNextCount(ELEMENTS_SIZE * 2).verifyComplete();

    // Then
    var latch0 = new AtomicInteger();
    stream.sub().take(ELEMENTS_SIZE).doOnNext(cmd -> {
      Assertions.assertThat(cmd.stateId()).isEqualTo(S0);
      Assertions.assertThat(cmd.partition(route.totalPartitions())).isEqualTo(route.topicPartition());
      latch0.incrementAndGet();
    }).subscribe();
    Awaitility.await().atMost(timeout).until(() -> latch0.get() == ELEMENTS_SIZE);

    // And
    var route1 = createRoute(1);
    var stream1 = CommandStream.create(route1, esStream, new SerializableTransformer(), SomeCommand.class);
    var latch1 = new AtomicInteger();
    stream1.sub().take(ELEMENTS_SIZE).doOnNext(cmd -> {
      Assertions.assertThat(cmd.stateId()).isEqualTo(S1);
      Assertions.assertThat(cmd.partition(route.totalPartitions())).isEqualTo(route1.topicPartition());
      latch1.incrementAndGet();
    }).subscribe();
    Awaitility.await().atMost(timeout).until(() -> latch1.get() == ELEMENTS_SIZE);
  }

  private static CommandRoute createRoute(int partition) {
    return new CommandRoute("command_topic", "events_topic", partition, 2);
  }

  private Flux<SomeCommand> createMessages(Id stateId) {
    return Flux.range(0, ELEMENTS_SIZE).map(i -> new SomeCommand(Id.of(i), stateId, Id.of(i)));
  }

  private record SomeCommand(Id eventId, Id stateId, Id commandId) implements Command {
    @Override
    public long timestamp() {
      return 0;
    }
  }
}
