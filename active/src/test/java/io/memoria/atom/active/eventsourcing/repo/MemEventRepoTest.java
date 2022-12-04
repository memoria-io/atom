package io.memoria.atom.active.eventsourcing.repo;

import io.memoria.atom.core.eventsourcing.*;
import io.vavr.collection.List;
import io.vavr.control.Try;
import org.junit.jupiter.api.MethodOrderer.OrderAnnotation;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;

import static org.junit.jupiter.api.Assertions.assertEquals;

@TestMethodOrder(OrderAnnotation.class)
class MemEventRepoTest {
  private static final int ELEMENTS_SIZE = 1000;
  private static final String topic = "some_topic";
  private static final int partition = 0;
  private static final StateId S0 = StateId.of(0);
  private static final StateId S1 = StateId.of(1);

  private final EventRepo<UserCreated> repo = new MemEventRepo<>(topic);

  @Test
  @Order(0)
  void publish() {
    // Given
    var events = createMsgs(S0).appendAll(createMsgs(S1));
    // Then
    events.zipWithIndex().map(tup -> repo.append(topic, tup._2, tup._1)).forEach(Try::get);
  }

  @Test
  @Order(1)
  void subscribe() {
    // Given
    var events = createMsgs(S0).appendAll(createMsgs(S1));
    // When
    events.zipWithIndex().map(tup -> repo.append(topic, tup._2, tup._1)).forEach(Try::get);
    // Then
    assertEquals(1000, repo.getAll(topic, S0).toList().size());
    assertEquals(1000, repo.getAll(topic, S1).toList().size());
  }

  private List<UserCreated> createMsgs(StateId stateId) {
    return List.range(0, ELEMENTS_SIZE).map(i -> new UserCreated(EventId.of(i), stateId));
  }

  record UserCreated(EventId eventId, StateId stateId) implements Event {

    @Override
    public CommandId commandId() {
      return CommandId.of(0);
    }

    @Override
    public long timestamp() {
      return 0;
    }
  }
}
