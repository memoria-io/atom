package io.memoria.atom.active.eventsourcing.infra.repo;

import io.memoria.atom.core.eventsourcing.StateId;
import io.memoria.atom.core.eventsourcing.infra.repo.ESRepoRow;
import io.vavr.collection.List;
import io.vavr.control.Try;
import org.junit.jupiter.api.MethodOrderer.OrderAnnotation;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;

import static org.junit.jupiter.api.Assertions.assertEquals;

@TestMethodOrder(OrderAnnotation.class)
class MemESRepoTest {
  private static final int ELEMENTS_SIZE = 1000;
  private static final String table = "some_topic";
  private static final StateId S0 = StateId.of(0);
  private static final StateId S1 = StateId.of(1);

  private final ESRepo repo = new MemESRepo(table);

  @Test
  @Order(0)
  void publish() {
    // Given
    var msgs = createMessages(S0).appendAll(createMessages(S1));
    // Then
    msgs.map(repo::append).forEach(Try::get);
  }

  @Test
  @Order(1)
  void subscribe() {
    // Given
    var msgs = createMessages(S0).appendAll(createMessages(S1));
    // When
    msgs.map(repo::append).forEach(Try::get);
    // Then
    assertEquals(1000, repo.getAll(table, S0.value()).toList().size());
    assertEquals(1000, repo.getAll(table, S1.value()).toList().size());
  }

  private List<ESRepoRow> createMessages(StateId stateId) {
    return List.range(0, ELEMENTS_SIZE).map(i -> new ESRepoRow(table, stateId.value(), i, "hello"));
  }
}
