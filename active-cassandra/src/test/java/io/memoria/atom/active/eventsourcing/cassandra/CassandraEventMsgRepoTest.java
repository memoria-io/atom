package io.memoria.atom.active.eventsourcing.cassandra;

import com.datastax.oss.driver.api.core.CqlSession;
import io.memoria.atom.active.eventsourcing.infra.event.EventMsg;
import io.memoria.atom.core.eventsourcing.StateId;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.MethodOrderer.OrderAnnotation;

import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.IntStream;

import static io.memoria.atom.active.eventsourcing.cassandra.TestUtils.KEYSPACE;

@TestMethodOrder(OrderAnnotation.class)
class CassandraEventMsgRepoTest {
  private static final String TOPIC = CassandraEventMsgRepoTest.class.getSimpleName() + "_events";
  private static final StateId STATE_ID = StateId.randomUUID();
  private static final CqlSession session = TestUtils.CqlSession();
  private static final CassandraEventRepoAdmin admin = new CassandraEventRepoAdmin(session);
  private static final CassandraEventMsgRepo repo = new CassandraEventMsgRepo(KEYSPACE, session);
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
    var rows = IntStream.range(0, COUNT).mapToObj(this::createMsg).map(repo::append);
    AtomicInteger idx = new AtomicInteger(0);
    // Then
    rows.forEach(i -> Assertions.assertEquals(i.get(), idx.getAndIncrement()));
  }

  @Test
  @Order(4)
  void get() {
    // When
    var count = repo.getAll(TOPIC, STATE_ID).count();
    // Then
    assert count == COUNT;
  }

  private EventMsg createMsg(int i) {
    return EventMsg.create(TOPIC, STATE_ID, i, "%d:message".formatted(i));
  }

  @BeforeAll
  static void beforeAll() {
    System.out.println("QueryClientTest: StateId:" + STATE_ID);
  }
}
