package io.memoria.atom.active.eventsourcing.cassandra;

import com.datastax.oss.driver.api.core.CqlSession;
import com.datastax.oss.driver.api.core.cql.ResultSet;
import com.datastax.oss.driver.api.core.cql.Row;
import io.memoria.atom.core.eventsourcing.StateId;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.MethodOrderer.OrderAnnotation;

import java.util.Objects;
import java.util.stream.IntStream;
import java.util.stream.StreamSupport;

import static io.memoria.atom.active.eventsourcing.cassandra.TestUtils.KEYSPACE;

@TestMethodOrder(OrderAnnotation.class)
class StatementsTest {
  private static final String TABLE = StatementsTest.class.getSimpleName() + "_events";
  private static final String STATE_ID = StateId.randomUUID().value();
  private static final CqlSession session = TestUtils.CqlSession();
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
    var st = Statements.createEventsKeyspace(KEYSPACE, 1);
    // When
    var isCreated = session.execute(st).wasApplied();
    // Then
    assert isCreated;
  }

  @Test
  @Order(3)
  void createTable() {
    // Given
    var st = Statements.createEventsTable(KEYSPACE, TABLE);
    // When
    var isCreated = session.execute(st).wasApplied();
    // Then
    assert isCreated;
  }

  @Test
  @Order(4)
  void push() {
    // Given
    var statements = IntStream.range(0, COUNT).mapToObj(i -> Statements.push(KEYSPACE, TABLE, createRow(i)));
    // When
    var isCreated = statements.map(st -> session.execute(st).wasApplied());
    // Then
    isCreated.forEach(Assertions::assertTrue);
  }

  @Test
  @Order(5)
  void get() {
    // Given
    var st = Statements.get(KEYSPACE, TABLE, STATE_ID);
    // When
    var rs = session.execute(st);
    var rows = StreamSupport.stream(rs.spliterator(), false);
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
