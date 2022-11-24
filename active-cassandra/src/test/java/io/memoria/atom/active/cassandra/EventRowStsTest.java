package io.memoria.atom.active.cassandra;

import com.datastax.oss.driver.api.core.CqlSession;
import com.datastax.oss.driver.api.core.cql.ResultSet;
import com.datastax.oss.driver.api.core.cql.Row;
import io.memoria.atom.active.cassandra.infra.ExecUtils;
import io.memoria.atom.core.eventsourcing.StateId;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.MethodOrderer.OrderAnnotation;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;

import java.util.Objects;
import java.util.stream.IntStream;

import static io.memoria.atom.active.cassandra.TestUtils.KEYSPACE;

@TestMethodOrder(OrderAnnotation.class)
class EventRowStsTest {
  private static final String TABLE = EventRowStsTest.class.getSimpleName() + "_events";
  private static final StateId STATE_ID = StateId.randomUUID();
  private static final CqlSession session = SessionUtils.session(TestUtils.getClientConfig()).build();
  private static final int COUNT = 100;

  @Test
  @Order(1)
  void testConnection() {
    // When
    ResultSet rs = session.execute("select release_version from system.local");
    Row row = rs.one();
    var version = Objects.requireNonNull(row).getString("release_version");
    // Then
    assert version != null && !version.isEmpty();
  }

  @Test
  @Order(2)
  void createKeyspace() {
    // Given
    var st = EventRowSts.createEventsKeyspace(KEYSPACE, 1);
    // When
    var isCreated = ExecUtils.exec(session, st);
    // Then
    assert isCreated;
  }

  @Test
  @Order(3)
  void createTable() {
    // Given
    var st = EventRowSts.createEventsTable(KEYSPACE, TABLE);
    // When
    var isCreated = ExecUtils.exec(session, st);
    // Then
    assert isCreated;
  }

  @Test
  @Order(4)
  void push() {
    // Given
    var statements = IntStream.range(0, COUNT).mapToObj(i -> EventRowSts.push(KEYSPACE, TABLE, createRow(i)));
    // When
    var isCreated = statements.map(st -> ExecUtils.exec(session, st));
    // Then
    isCreated.forEach(Assertions::assertTrue);
  }

  @Test
  @Order(5)
  void get() {
    // Given
    var st = EventRowSts.get(KEYSPACE, TABLE, STATE_ID);
    // When
    var rows = ExecUtils.execSelect(session, st);
    // Then
    assert rows.count() == COUNT;
  }

  @BeforeAll
  static void beforeAll() {
    System.out.println("EventRowStsTest: StateId:" + STATE_ID);
  }

  private static EventRow createRow(int i) {
    return new EventRow(STATE_ID, i, "{some event happened here}");
  }
}
