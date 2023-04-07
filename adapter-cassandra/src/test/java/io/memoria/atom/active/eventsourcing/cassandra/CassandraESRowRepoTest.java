package io.memoria.atom.active.eventsourcing.cassandra;

import com.datastax.oss.driver.api.core.CqlSession;
import io.memoria.atom.core.eventsourcing.StateId;
import io.memoria.atom.core.eventsourcing.pipeline.repo.ESRow;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.MethodOrderer.OrderAnnotation;
import reactor.core.publisher.Flux;
import reactor.test.StepVerifier;

import java.util.concurrent.atomic.AtomicInteger;

import static io.memoria.atom.active.eventsourcing.cassandra.TestUtils.KEYSPACE;

@TestMethodOrder(OrderAnnotation.class)
class CassandraESRowRepoTest {
  private static final String TOPIC = CassandraESRowRepoTest.class.getSimpleName() + "_events";
  private static final StateId STATE_ID = StateId.randomUUID();
  private static final CqlSession session = TestUtils.CqlSession();
  private static final CassandraESRepoAdmin admin = new CassandraESRepoAdmin(session);
  private static final CassandraESRowRepo repo = new CassandraESRowRepo(KEYSPACE, session);
  private static final int COUNT = 100;

  @Test
  @Order(1)
  void createKeyspace() {
    // When
    var isCreated = admin.createKeyspace(KEYSPACE, 1);
    // Then
    assert isCreated;
  }

  @Test
  @Order(2)
  void createTable() {
    // When
    var isCreated = admin.createTopicTable(KEYSPACE, TOPIC);
    // Then
    assert isCreated;
  }

  @Test
  @Order(3)
  void push() {
    // When
    var rows = Flux.range(0, COUNT).flatMap(i -> repo.append(TOPIC, STATE_ID.value(), String.valueOf(i)));
    AtomicInteger idx = new AtomicInteger(0);
    // Then
    StepVerifier.create(rows.map(ESRow::seqId)).expectNext(idx.getAndIncrement()).verifyComplete();
  }

  @Test
  @Order(4)
  void get() {
    // When
    // Then
    StepVerifier.create(repo.getAll(TOPIC, STATE_ID.value())).expectNextCount(COUNT).verifyComplete();
  }

  @BeforeAll
  static void beforeAll() {
    System.out.println("QueryClientTest: StateId:" + STATE_ID);
  }
}
