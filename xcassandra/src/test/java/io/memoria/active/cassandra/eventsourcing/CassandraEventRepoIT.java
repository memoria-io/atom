package io.memoria.active.cassandra.eventsourcing;

import com.datastax.oss.driver.api.core.CqlSession;
import io.memoria.active.cassandra.CassandraUtils;
import io.memoria.active.cassandra.Infra;
import io.memoria.atom.core.text.SerializableTransformer;
import io.memoria.atom.core.text.TextException;
import io.memoria.atom.eventsourcing.command.CommandId;
import io.memoria.atom.eventsourcing.event.Event;
import io.memoria.atom.eventsourcing.event.EventId;
import io.memoria.atom.eventsourcing.event.EventMeta;
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
  private static final String KEYSPACE = "eventsourcing";
  private static final String TABLE = "events2" + System.currentTimeMillis();
  private static final StateId STATE_ID = StateId.of("aggId");
  private static final CqlSession session = Infra.cqlSession();
  private static final CassandraEventRepo repo = new CassandraEventRepo(session,
                                                                        KEYSPACE,
                                                                        TABLE,
                                                                        new SerializableTransformer());
  private static final int COUNT = 1000;
  private static final List<Event> events = IntStream.range(0, COUNT).mapToObj(CassandraEventRepoIT::toEvent).toList();

  @BeforeAll
  static void beforeAll() {
    assert session.execute(CassandraUtils.createKeyspace(KEYSPACE, 1)).wasApplied();
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
  void stream() throws TextException {
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

  private static Event toEvent(int i) {
    return new Event() {
      @Override
      public EventMeta meta() {
        return new EventMeta(EventId.of(i), 0, STATE_ID, CommandId.of(0));
      }
    };
  }
}
