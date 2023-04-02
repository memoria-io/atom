package io.memoria.reactive.eventsourcing.infra.repo;

import io.memoria.atom.core.eventsourcing.StateId;
import io.memoria.atom.core.eventsourcing.infra.repo.ESRepoRow;
import org.junit.jupiter.api.MethodOrderer.OrderAnnotation;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import reactor.core.publisher.Flux;
import reactor.test.StepVerifier;

@TestMethodOrder(OrderAnnotation.class)
class MemESRepoTest {
  private static final int ELEMENTS_SIZE = 1000;
  private static final String table = "some_topic";
  private static final StateId S0 = StateId.of(0);
  private static final StateId S1 = StateId.of(1);

  private final ESRepo repo = new MemESRepo(table);

  @Test
  void appendAndGet() {
    // Given
    var msgs = createMessages(S0).concatWith(createMessages(S1));
    // When, Then
    StepVerifier.create(msgs.map(repo::append)).expectNextCount(ELEMENTS_SIZE * 2).verifyComplete();
    // and
    StepVerifier.create(repo.getAll(table, S0.value())).expectNextCount(ELEMENTS_SIZE).verifyComplete();
    StepVerifier.create(repo.getAll(table, S1.value())).expectNextCount(ELEMENTS_SIZE).verifyComplete();
  }

  private Flux<ESRepoRow> createMessages(StateId stateId) {
    return Flux.range(0, ELEMENTS_SIZE).map(i -> new ESRepoRow(table, stateId.value(), i, "hello"));
  }
}
