package io.memoria.reactive.eventsourcing.infra.repo;

import io.memoria.atom.core.eventsourcing.*;
import io.memoria.atom.core.text.SerializableTransformer;
import org.junit.jupiter.api.Test;
import reactor.core.publisher.Flux;
import reactor.test.StepVerifier;

class EventRepoImplTest {
  private static final int ELEMENTS_SIZE = 1000;
  private static final StateId S0 = StateId.of(0);
  private static final StateId S1 = StateId.of(1);

  private final Route route = new Route("command_topic", 0, 1, "event_topic");
  private final ESRepo esRepo = ESRepo.inMemory(route.eventTable());
  private final EventRepo<SomeEvent> eventRepo = EventRepo.create(route,
                                                                  esRepo,
                                                                  new SerializableTransformer(),
                                                                  SomeEvent.class);

  @Test
  void appendAndGet() {
    // Given
    var events = createMessages(S0).concatWith(createMessages(S1))
                                   .index()
                                   .flatMap(tup -> eventRepo.append(tup.getT1().intValue(), tup.getT2()));
    // When
    StepVerifier.create(events).expectNextCount(ELEMENTS_SIZE * 2).verifyComplete();

    // Then
    StepVerifier.create(eventRepo.getAll(S0)).expectNextCount(ELEMENTS_SIZE).verifyComplete();
    StepVerifier.create(eventRepo.getAll(S1)).expectNextCount(ELEMENTS_SIZE).verifyComplete();
  }

  private Flux<SomeEvent> createMessages(StateId stateId) {
    return Flux.range(0, ELEMENTS_SIZE).map(i -> new SomeEvent(EventId.of(i), stateId, CommandId.of(i)));
  }

  private record SomeEvent(EventId eventId, StateId stateId, CommandId commandId) implements Event {
    @Override
    public long timestamp() {
      return 0;
    }
  }
}
