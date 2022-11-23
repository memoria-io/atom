package io.memoria.atom.es.active.repo.mem;

import io.memoria.atom.core.eventsourcing.CommandId;
import io.memoria.atom.core.eventsourcing.Event;
import io.memoria.atom.core.eventsourcing.EventId;
import io.memoria.atom.core.eventsourcing.StateId;
import io.vavr.collection.List;
import io.vavr.control.Try;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.MethodOrderer.OrderAnnotation;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;

import static org.junit.jupiter.api.Assertions.assertEquals;

@TestMethodOrder(OrderAnnotation.class)
class MemEventRepoTest {
  private static final int ELEMENTS_SIZE = 1000;
  private static final int partition = 0;
  private static final StateId S0 = StateId.of(0);
  private static final StateId S1 = StateId.of(1);
  private static final MemEventRepo<Event> repo = new MemEventRepo<>(1);

  @AfterEach
  void afterEach() {
    repo.topic.get(partition).msgs().clear();
  }

  @Test
  @Order(0)
  void publish() {
    // Given
    var events = createMsgs(S0).appendAll(createMsgs(S1));
    // Then
    events.map(repo::push).forEach(Try::get);
  }

  @Test
  @Order(1)
  void subscribe() {
    // Given
    var events = createMsgs(S0).appendAll(createMsgs(S1));
    // When
    events.map(repo::push).forEach(Try::get);
    // Then
    assertEquals(1000, repo.get(S0).toList().size());
    assertEquals(1000, repo.get(S1).toList().size());
  }

  private List<Event> createMsgs(StateId stateId) {
    return List.range(0, ELEMENTS_SIZE).map(i -> new Ev(EventId.of(i), CommandId.of(i), stateId));
  }

  private record Ev(EventId eventId, CommandId commandId, StateId stateId) implements Event {

    @Override
    public long timestamp() {
      return 0;
    }
  }
}
