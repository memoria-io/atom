package io.memoria.atom.active.eventsourcing.adapter.repo;

import io.memoria.atom.active.eventsourcing.infra.repo.ESRepo;
import io.memoria.atom.core.eventsourcing.*;
import io.memoria.atom.core.text.SerializableTransformer;
import io.vavr.collection.List;
import io.vavr.control.Try;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class EventRepoImplTest {
  private static final int ELEMENTS_SIZE = 1000;
  private static final StateId S0 = StateId.of(0);
  private static final StateId S1 = StateId.of(1);

  private final Route route = new Route("command_topic", 0, 1, "event_topic");
  private final EventRepo<SomeEvent> eventRepo = EventRepo.create(route,
                                                                  ESRepo.inMemory(route.eventTable()),
                                                                  new SerializableTransformer(),
                                                                  SomeEvent.class);

  @Test
  @Order(0)
  void publish() {
    // Given
    var events = createMessages(S0).appendAll(createMessages(S1));
    // Then
    events.zipWithIndex().map(tup -> eventRepo.append(tup._2, tup._1)).forEach(Try::get);
  }

  @Test
  @Order(1)
  void subscribe() {
    // Given
    var events = createMessages(S0).appendAll(createMessages(S1));
    // When
    events.zipWithIndex().map(tup -> eventRepo.append(tup._2, tup._1)).forEach(Try::get);
    // Then
    assertEquals(1000, eventRepo.getAll(S0).toList().size());
    assertEquals(1000, eventRepo.getAll(S1).toList().size());
  }

  private List<SomeEvent> createMessages(StateId stateId) {
    return List.range(0, ELEMENTS_SIZE).map(i -> new SomeEvent(EventId.of(i), stateId, CommandId.of(i)));
  }

  private record SomeEvent(EventId eventId, StateId stateId, CommandId commandId) implements Event {
    @Override
    public long timestamp() {
      return 0;
    }
  }
}
