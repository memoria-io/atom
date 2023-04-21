package io.memoria.atom.eventsourcing.stream;

import io.memoria.atom.core.id.Id;
import io.memoria.atom.core.stream.ESMsgStream;
import io.memoria.atom.core.text.SerializableTransformer;
import io.memoria.atom.eventsourcing.Command;
import io.memoria.atom.eventsourcing.pipeline.PipelineRoute;
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

  private final ESMsgStream esStream = ESMsgStream.inMemory();

  @Test
  void publishAndSubscribe() {
    // Given
    var route = createRoute(0);
    var cmdStream = getStream(route, esStream);
    var cmds = createMessages(S0).concatWith(createMessages(S1));

    // When
    StepVerifier.create(cmds.flatMap(cmdStream::pub)).expectNextCount(ELEMENTS_SIZE * 2).verifyComplete();

    // Then
    var latch0 = new AtomicInteger();
    cmdStream.sub().take(ELEMENTS_SIZE).doOnNext(cmd -> {
      Assertions.assertThat(cmd.stateId()).isEqualTo(S0);
      Assertions.assertThat(cmd.partition(route.cmdTotalPubPartitions())).isEqualTo(route.cmdSubPartition());
      latch0.incrementAndGet();
    }).subscribe();
    Awaitility.await().atMost(timeout).until(() -> latch0.get() == ELEMENTS_SIZE);

    // And
    var route1 = createRoute(1);
    var stream1 = getStream(route1, esStream);
    var latch1 = new AtomicInteger();
    stream1.sub().take(ELEMENTS_SIZE).doOnNext(cmd -> {
      Assertions.assertThat(cmd.stateId()).isEqualTo(S1);
      Assertions.assertThat(cmd.partition(route.cmdTotalPubPartitions())).isEqualTo(route1.cmdSubPartition());
      latch1.incrementAndGet();
    }).subscribe();
    Awaitility.await().atMost(timeout).until(() -> latch1.get() == ELEMENTS_SIZE);
  }

  private static CommandStream<SomeCommand> getStream(PipelineRoute route, ESMsgStream esStream) {
    return CommandStream.create(route, esStream, new SerializableTransformer(), SomeCommand.class);
  }

  private static PipelineRoute createRoute(int partition) {
    return new PipelineRoute("command_topic", partition, 2, "events_topic", partition);
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
