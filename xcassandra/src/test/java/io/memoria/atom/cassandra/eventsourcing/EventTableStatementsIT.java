package io.memoria.atom.cassandra.eventsourcing;

import com.datastax.oss.driver.api.core.CqlSession;
import com.datastax.oss.driver.api.core.cql.ResultSet;
import com.datastax.oss.driver.api.core.cql.Row;
import io.memoria.atom.cassandra.CassandraUtils;
import io.memoria.atom.cassandra.Infra;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.MethodOrderer.OrderAnnotation;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;

import java.util.Objects;
import java.util.stream.IntStream;
import java.util.stream.StreamSupport;

@TestMethodOrder(value = OrderAnnotation.class)
class EventTableStatementsIT {
  private static final String KEYSPACE = "eventsourcing";
  private static final String TABLE = "events" + System.currentTimeMillis();
  private static final String PARTITION_KEY = "aggId";
  private static final CqlSession session = Infra.cqlSession();
  private static final int COUNT = 100;

  @BeforeAll
  static void beforeAll() {
    // Check connection
    ResultSet rs = session.execute("select release_version from system.local");
    Row row = rs.one();
    var version = Objects.requireNonNull(row).getString("release_version");
    assert version != null && !version.isEmpty();

    // Create namespace
    var st = CassandraUtils.createKeyspace(KEYSPACE, 1);
    var keyspaceCreated = session.execute(st).wasApplied();
    assert keyspaceCreated;

    // Create table
    var tableCreated = session.execute(EventTableStatements.createTable(KEYSPACE, TABLE)).wasApplied();
    assert tableCreated;
  }

  @Test
  @Order(0)
  void push() {
    // When
    var statements = IntStream.range(0, COUNT)
                              .mapToObj(i -> EventTableStatements.push(KEYSPACE, TABLE, PARTITION_KEY, i, "hello world"));
    // Then
    Assertions.assertThatCode(() -> statements.forEach(session::execute)).doesNotThrowAnyException();
  }

  @Test
  @Order(1)
  void fetchAll() {
    // When
    var rs = session.execute(EventTableStatements.fetchAll(KEYSPACE, TABLE, PARTITION_KEY, 0));
    var rows = StreamSupport.stream(rs.spliterator(), false);
    // Then
    Assertions.assertThat(rows.count()).isEqualTo(COUNT);
  }

  @Test
  @Order(2)
  void getWithOffset() {
    // Given
    int startIdx = 2;
    // When
    var rows = session.execute(EventTableStatements.fetchAll(KEYSPACE, TABLE, PARTITION_KEY, startIdx));
    // Then
    Assertions.assertThat(rows.all()).hasSize(COUNT - startIdx);
  }

  @Test
  void getLast() {
    // When
    var lastSeq = session.execute(EventTableStatements.getLast(KEYSPACE, TABLE, PARTITION_KEY))
                         .map(row -> row.getLong(EventTableStatements.CLUSTER_KEY_COL))
                         .one();
    // Then
    Assertions.assertThat(lastSeq).isEqualTo(COUNT - 1);
  }

  @Test
  void getLastButUnknown() {
    // Given
    var st = EventTableStatements.getLast(KEYSPACE, TABLE, "unknown");
    // When
    var spliterator = session.execute(st).spliterator();
    var count = StreamSupport.stream(spliterator, false).count();
    // Then
    Assertions.assertThat(count).isZero();
  }
}
