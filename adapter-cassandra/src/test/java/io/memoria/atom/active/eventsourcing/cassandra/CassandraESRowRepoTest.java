package io.memoria.atom.active.eventsourcing.cassandra;

import com.datastax.oss.driver.api.core.CqlSession;
import io.memoria.atom.core.eventsourcing.StateId;
import io.memoria.atom.core.eventsourcing.pipeline.repo.ESRow;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import reactor.core.publisher.Flux;
import reactor.test.StepVerifier;

import static io.memoria.atom.active.eventsourcing.cassandra.TestUtils.KEYSPACE;

class CassandraESRowRepoTest {
  private static final String table = CassandraESRowRepoTest.class.getSimpleName() + "_events";
  private static final CqlSession session = TestUtils.CqlSession();
  private static final CassandraESRepoAdmin admin = new CassandraESRepoAdmin(session);
  private static final CassandraESRowRepo repo = new CassandraESRowRepo(KEYSPACE, session);
  private static final int COUNT = 100;

  @BeforeAll
  static void beforeAll() {
    // Create Keyspace
    var keyspaceCreated = admin.createKeyspace(KEYSPACE, 1);
    assert keyspaceCreated;
    // Create Table
    var tableCreated = admin.createTopicTable(KEYSPACE, table);
    assert tableCreated;
  }

  @Test
  void append() {
    // Given
    var stateId = StateId.randomUUID().value();
    var rows = Flux.range(0, COUNT).map(i -> new ESRow(table, stateId, i, String.valueOf(i)));
    StepVerifier.create(repo.append(table, rows)).expectNextCount(COUNT).verifyComplete();
    // When, Then
    StepVerifier.create(rows.map(ESRow::seqId)).expectNextCount(COUNT).verifyComplete();
  }

  @Test
  void getAll() {
    // Given
    var stateId = StateId.randomUUID().value();
    var rowFlux = Flux.range(0, COUNT)
                      .map(i -> new ESRow(table, stateId, i, "hello world"))
                      .flatMap(row -> repo.appendESRow(KEYSPACE, row));
    StepVerifier.create(rowFlux).expectNextCount(COUNT).verifyComplete();
    // When, Then
    StepVerifier.create(repo.getAll(table, stateId)).expectNextCount(COUNT).verifyComplete();
  }

  @Test
  void appendESRow() {
    // Given
    var stateId = StateId.randomUUID().value();
    var rowFlux = Flux.range(0, COUNT)
                      .map(i -> new ESRow(table, stateId, i, "hello world"))
                      .flatMap(row -> repo.appendESRow(KEYSPACE, row));
    // When, Then
    StepVerifier.create(rowFlux).expectNextCount(COUNT).verifyComplete();
    StepVerifier.create(repo.getAll(table, stateId).count()).expectNext((long) COUNT).verifyComplete();
  }
}
