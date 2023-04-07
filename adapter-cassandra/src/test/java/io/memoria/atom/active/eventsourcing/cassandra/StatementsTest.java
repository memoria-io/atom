package io.memoria.atom.active.eventsourcing.cassandra;

import com.datastax.dse.driver.api.core.cql.reactive.ReactiveRow;
import com.datastax.oss.driver.api.core.CqlSession;
import com.datastax.oss.driver.api.core.cql.ResultSet;
import com.datastax.oss.driver.api.core.cql.Row;
import io.memoria.atom.core.eventsourcing.StateId;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import reactor.core.publisher.Flux;
import reactor.test.StepVerifier;

import java.util.Objects;
import java.util.stream.StreamSupport;

import static io.memoria.atom.active.eventsourcing.cassandra.TestUtils.KEYSPACE;

class StatementsTest {
  private static final String TABLE = StatementsTest.class.getSimpleName() + "_table";
  private static final CqlSession session = TestUtils.CqlSession();
  private static final int COUNT = 100;

  @BeforeAll
  static void beforeAll() {
    // Check connection
    ResultSet rs = session.execute("select release_version from system.local");
    Row row = rs.one();
    var version = Objects.requireNonNull(row).getString("release_version");
    assert version != null && !version.isEmpty();

    // Create namespace
    var st = Statements.createEventsKeyspace(KEYSPACE, 1);
    var keyspaceCreated = session.execute(st).wasApplied();
    assert keyspaceCreated;

    // Create table
    var tableCreated = session.execute(Statements.createEventsTable(KEYSPACE, TABLE)).wasApplied();
    assert tableCreated;
  }

  @Test
  void pushReactive() {
    // Given
    var stateId = StateId.randomUUID().value();
    var statements = Flux.range(0, COUNT).map(i -> Statements.push(KEYSPACE, TABLE, createRow(stateId, i)));
    // When
    var isCreatedFlux = statements.flatMap(session::executeReactive).map(ReactiveRow::wasApplied);
    // Then
    StepVerifier.create(isCreatedFlux).expectNextCount(COUNT).verifyComplete();
  }

  @Test
  void getAll() {
    // Given
    var stateId = StateId.randomUUID().value();
    var statements = Flux.range(0, COUNT).map(i -> Statements.push(KEYSPACE, TABLE, createRow(stateId, i)));
    var isCreatedFlux = statements.flatMap(session::executeReactive).map(ReactiveRow::wasApplied);
    StepVerifier.create(isCreatedFlux).expectNextCount(COUNT).verifyComplete();
    // When
    var rs = session.execute(Statements.get(KEYSPACE, TABLE, stateId, 0));
    var rows = StreamSupport.stream(rs.spliterator(), false);
    // Then
    assert rows.count() == COUNT;
  }

  @Test
  void getWithOffset() {
    // Given
    int startIdx = 2;
    // Given
    var stateId = StateId.randomUUID().value();
    var statements = Flux.range(0, COUNT).map(i -> Statements.push(KEYSPACE, TABLE, createRow(stateId, i)));
    var isCreatedFlux = statements.flatMap(session::executeReactive).map(ReactiveRow::wasApplied);
    StepVerifier.create(isCreatedFlux).expectNextCount(COUNT).verifyComplete();
    // When
    var rowFlux = Flux.from(session.executeReactive(Statements.get(KEYSPACE, TABLE, stateId, startIdx)))
                      .map(CassandraRow::from);
    // Then
    StepVerifier.create(rowFlux).expectNextCount(COUNT - 2).verifyComplete();
  }

  @Test
  void get() {
    // Given
    var stateId = StateId.randomUUID().value();
    var statements = Flux.range(0, COUNT).map(i -> Statements.push(KEYSPACE, TABLE, createRow(stateId, i)));
    var isCreatedFlux = statements.flatMap(session::executeReactive).map(ReactiveRow::wasApplied);
    StepVerifier.create(isCreatedFlux).expectNextCount(COUNT).verifyComplete();
    // When
    var lastSeq = Flux.from(session.executeReactive(Statements.getLastSeqId(KEYSPACE, TABLE, stateId)))
                      .map(CassandraRow::from)
                      .map(CassandraRow::seqId);
    // Then
    StepVerifier.create(lastSeq).expectNext(COUNT - 1).verifyComplete();
  }

  @Test
  void getLastButUnknown() {
    // Given
    var st = Statements.getLastSeqId(KEYSPACE, TABLE, "unknown");
    // When
    var exec = Flux.from(session.executeReactive(st)).map(ReactiveRow::wasApplied);
    // Then
    StepVerifier.create(exec).verifyComplete();
  }

  private static CassandraRow createRow(String stateId, int i) {
    return new CassandraRow(stateId, i, "{some event happened here}");
  }
}
