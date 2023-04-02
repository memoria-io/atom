package io.memoria.atom.active.eventsourcing.infra.repo;

import io.memoria.atom.core.eventsourcing.*;
import io.memoria.atom.core.eventsourcing.infra.CRoute;
import io.memoria.atom.core.text.SerializableTransformer;
import io.vavr.collection.List;
import io.vavr.control.Try;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class EventRepoImplTest {
  private static final int ELEMENTS_SIZE = 1000;
  private static final StateId S0 = StateId.of(0);
  private static final StateId S1 = StateId.of(1);

  private final CRoute CRoute = new CRoute("command_topic", 0, 1, "event_topic");
  private final EventRepo<SomeEvent> eventRepo = EventRepo.create(CRoute,
                                                                  ESRepo.inMemory(CRoute.eventTable()),
                                                                  new SerializableTransformer(),
                                                                  SomeEvent.class);

  @Test
  void appendAndGet() {
    // Given
    var events = createMessages(S0).appendAll(createMessages(S1));
    // When
    events.zipWithIndex().map(tup -> eventRepo.append(tup._2, tup._1)).forEach(Try::get);
    // Then
    assertEquals(ELEMENTS_SIZE, eventRepo.getAll(S0).toList().size());
    assertEquals(ELEMENTS_SIZE, eventRepo.getAll(S1).toList().size());
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
