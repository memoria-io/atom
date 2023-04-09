package io.memoria.atom.eventsourcing.pipeline.stream;

import io.memoria.atom.core.eventsourcing.*;
import io.memoria.atom.core.stream.ESMsgStream;
import io.memoria.atom.eventsourcing.*;
import io.memoria.atom.eventsourcing.pipeline.CommandRoute;
import io.memoria.atom.core.text.SerializableTransformer;
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
  private static final StateId S0 = StateId.of(0);
  private static final StateId S1 = StateId.of(1);

  @Test
  void publishAndSubscribe() {
    // Given
    var route = createRoute(0);
    var esStream = ESMsgStream.inMemory(route.cmdTopic(), route.cmdTopicPartitions());
    var stream = CommandStream.create(route, esStream, new SerializableTransformer(), SomeCommand.class);
    var msgs = createMessages(S0).concatWith(createMessages(S1));

    // When
    StepVerifier.create(msgs.flatMap(stream::pub)).expectNextCount(ELEMENTS_SIZE * 2).verifyComplete();

    // Then
    var latch0 = new AtomicInteger();
    stream.sub().take(ELEMENTS_SIZE).doOnNext(cmd -> {
      Assertions.assertThat(cmd.stateId()).isEqualTo(S0);
      Assertions.assertThat(cmd.partition(route.cmdTopicPartitions())).isEqualTo(route.cmdTopicPartition());
      latch0.incrementAndGet();
    }).subscribe();
    Awaitility.await().atMost(timeout).until(() -> latch0.get() == ELEMENTS_SIZE);

    // And
    var route1 = createRoute(1);
    var stream1 = CommandStream.create(route1, esStream, new SerializableTransformer(), SomeCommand.class);
    var latch1 = new AtomicInteger();
    stream1.sub().take(ELEMENTS_SIZE).doOnNext(cmd -> {
      Assertions.assertThat(cmd.stateId()).isEqualTo(S1);
      Assertions.assertThat(cmd.partition(route.cmdTopicPartitions())).isEqualTo(route1.cmdTopicPartition());
      latch1.incrementAndGet();
    }).subscribe();
    Awaitility.await().atMost(timeout).until(() -> latch1.get() == ELEMENTS_SIZE);
  }

  private static CommandRoute createRoute(int cmdPartition) {
    return new CommandRoute("events_table", "events_topic", 0, 1, "command_topic", cmdPartition, 2);
  }

  private Flux<SomeCommand> createMessages(StateId stateId) {
    return Flux.range(0, ELEMENTS_SIZE).map(i -> new SomeCommand(EventId.of(i), stateId, CommandId.of(i)));
  }

  private record SomeCommand(EventId eventId, StateId stateId, CommandId commandId) implements Command {
    @Override
    public long timestamp() {
      return 0;
    }
  }
}
