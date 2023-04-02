package io.memoria.atom.active.eventsourcing.infra.stream;

import io.memoria.atom.core.eventsourcing.*;
import io.memoria.atom.core.eventsourcing.infra.CRoute;
import io.memoria.atom.core.text.SerializableTransformer;
import io.vavr.collection.List;
import io.vavr.control.Try;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class CommandStreamImplTest {
  private static final int ELEMENTS_SIZE = 1000;
  private static final StateId S0 = StateId.of(0);
  private static final StateId S1 = StateId.of(1);

  @Test
  @Order(0)
  void publish() {
    var route = createRoute(0);
    var stream = CommandStream.create(route,
                                      ESStream.inMemory(route.cmdTopic(), route.cmdTopicTotalPartitions()),
                                      new SerializableTransformer(),
                                      SomeCommand.class);
    // Given
    var msgs = createMessages(S0).appendAll(createMessages(S1));
    // Then
    msgs.map(stream::pub).forEach(Try::get);
  }

  @Test
  @Order(1)
  void subscribe() {
    // Given
    var route = createRoute(0);
    var esStream = ESStream.inMemory(route.cmdTopic(), route.cmdTopicTotalPartitions());
    var stream = CommandStream.create(route, esStream,
                                      new SerializableTransformer(),
                                      SomeCommand.class);
    var msgs = createMessages(S0).appendAll(createMessages(S1));
    // When
    msgs.map(stream::pub).forEach(Try::get);
    // Then
    stream.sub().limit(ELEMENTS_SIZE).forEachOrdered(msg -> {
      assertEquals(S0, msg.get().stateId());
      assertEquals(0, msg.get().partition(route.cmdTopicTotalPartitions()));
    });


    // And
    var route1 = createRoute(1);
    var stream1 = CommandStream.create(route1,
                                  esStream,
                                  new SerializableTransformer(),
                                  SomeCommand.class);
    stream1.sub().limit(ELEMENTS_SIZE).forEachOrdered(msg -> {
      assertEquals(S1, msg.get().stateId());
      assertEquals(1, msg.get().partition(route.cmdTopicTotalPartitions()));
    });
  }

  private static CRoute createRoute(int cmdPartition) {
    return new CRoute("command_topic", cmdPartition, 2, "event_topic");
  }

  private List<SomeCommand> createMessages(StateId stateId) {
    return List.range(0, ELEMENTS_SIZE).map(i -> new SomeCommand(EventId.of(i), stateId, CommandId.of(i)));
  }

  private record SomeCommand(EventId eventId, StateId stateId, CommandId commandId) implements Command {
    @Override
    public long timestamp() {
      return 0;
    }
  }
}
