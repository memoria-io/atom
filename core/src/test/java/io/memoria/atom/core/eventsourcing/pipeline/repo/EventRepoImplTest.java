package io.memoria.atom.core.eventsourcing.pipeline.repo;

import io.memoria.atom.core.eventsourcing.*;
import io.memoria.atom.core.text.SerializableTransformer;
import org.junit.jupiter.api.Test;
import reactor.core.publisher.Flux;
import reactor.test.StepVerifier;

class EventRepoImplTest {
  private static final int ELEMENTS_SIZE = 1000;
  private static final StateId S0 = StateId.of(0);
  private static final StateId S1 = StateId.of(1);
  private static final String eventTable = "events";
  private final EventRepo<SomeEvent> eventRepo = EventRepo.create(eventTable,
                                                                  ESRowRepo.inMemory(eventTable),
                                                                  new SerializableTransformer(),
                                                                  SomeEvent.class);

  @Test
  void appendAndGet() {
    // Given
    var events = createEvents(S0).concatWith(createEvents(S1));
    // When
    var eventFlux = eventRepo.append(events);
    StepVerifier.create(eventFlux).expectNextCount(ELEMENTS_SIZE * 2).verifyComplete();
    // Then
    StepVerifier.create(eventRepo.getAll(S0)).expectNextCount(ELEMENTS_SIZE).verifyComplete();
    StepVerifier.create(eventRepo.getAll(S1)).expectNextCount(ELEMENTS_SIZE).verifyComplete();
  }

  private Flux<SomeEvent> createEvents(StateId stateId) {
    return Flux.range(0, ELEMENTS_SIZE).map(i -> new SomeEvent(EventId.of(i), i, stateId, CommandId.of(i)));
  }

  private record SomeEvent(EventId eventId, int seqId, StateId stateId, CommandId commandId) implements Event {
    @Override
    public long timestamp() {
      return 0;
    }
  }
}
