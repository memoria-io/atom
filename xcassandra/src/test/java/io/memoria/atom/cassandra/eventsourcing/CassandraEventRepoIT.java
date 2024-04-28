package io.memoria.atom.cassandra.eventsourcing;

import com.datastax.oss.driver.api.core.CqlSession;
import io.memoria.atom.cassandra.Infra;
import io.memoria.atom.cassandra.XCassandra;
import io.memoria.atom.core.text.SerializableTransformer;
import io.memoria.atom.eventsourcing.event.Event;
import io.memoria.atom.eventsourcing.state.StateId;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.MethodOrderer.OrderAnnotation;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;

import java.util.List;
import java.util.stream.IntStream;

@TestMethodOrder(OrderAnnotation.class)
class CassandraEventRepoIT {
  private static final String KEYSPACE = "event_sourcing";
  private static final String TABLE = STR."events2\{System.currentTimeMillis()}";
  private static final StateId STATE_ID = StateId.of("aggId");
  private static final CqlSession session = Infra.cqlSession();
  private static final CassandraEventRepo repo = new CassandraEventRepo(session,
                                                                        KEYSPACE,
                                                                        TABLE,
                                                                        new SerializableTransformer());
  private static final int COUNT = 5;
  private static final List<Event> events = IntStream.range(0, COUNT).mapToObj(CassandraEventRepoIT::toEvent).toList();

  @BeforeAll
  static void beforeAll() {
    assert session.execute(XCassandra.createKeyspace(KEYSPACE, 1)).wasApplied();
    assert session.execute(EventTableStatements.createTable(KEYSPACE, TABLE)).wasApplied();
  }

  @Test
  @Order(0)
  void append() {
    Assertions.assertThatCode(() -> {
      for (Event event : events) {
        repo.append(event);
      }
    }).doesNotThrowAnyException();
  }

  @Test
  @Order(1)
  void stream() {
    // When
    var result = repo.fetch(STATE_ID);

    // Then
    Assertions.assertThat(result).containsExactlyElementsOf(events);
  }

  @Test
  @Order(2)
  void size() {
    // When
    var result = repo.size(STATE_ID);

    // Then
    Assertions.assertThat(result).isEqualTo(COUNT);
  }

  private static Event toEvent(int version) {
    return new SomeEvent(version, STATE_ID);
  }
}
