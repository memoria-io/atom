package io.memoria.atom.active.eventsourcing.infra.stream;

import io.memoria.atom.core.eventsourcing.StateId;
import io.memoria.atom.core.eventsourcing.infra.stream.ESStreamMsg;
import io.vavr.collection.List;
import io.vavr.control.Try;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class MemESStreamTest {
  private static final int ELEMENTS_SIZE = 1000;
  private static final String topic = "some_topic";
  private static final StateId S0 = StateId.of(0);
  private static final StateId S1 = StateId.of(1);
  private static final int TOTAL_PARTITIONS = 2;

  private final ESStream stream = new MemESStream(topic, TOTAL_PARTITIONS);

  @Test
  @Order(0)
  void publish() {
    // Given
    var msgs = createMessages(S0).appendAll(createMessages(S1));
    // Then
    msgs.map(stream::pub).forEach(Try::get);
  }

  @Test
  @Order(1)
  void subscribe() {
    // Given
    var msgs = createMessages(S0).appendAll(createMessages(S1));
    // When
    msgs.map(stream::pub).forEach(Try::get);
    // Then
    stream.sub(topic, 0).limit(1000).forEachOrdered(msg -> {
      assertEquals(S0.value(), msg.key());
      assertEquals(0, msg.partition());
    });
    stream.sub(topic, 1).limit(1000).forEachOrdered(msg -> {
      assertEquals(S1.value(), msg.key());
      assertEquals(1, msg.partition());
    });
    System.out.println("done");
  }

  private List<ESStreamMsg> createMessages(StateId stateId) {
    return List.range(0, ELEMENTS_SIZE)
               .map(i -> new ESStreamMsg(topic, getPartition(stateId), stateId.value(), "hello"));
  }

  private static int getPartition(StateId stateId) {
    return Integer.parseInt(stateId.value()) % TOTAL_PARTITIONS;
  }
}
