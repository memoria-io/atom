package io.memoria.atom.active.eventsourcing.cassandra;

import com.datastax.oss.driver.api.core.CqlSession;
import io.memoria.atom.active.eventsourcing.repo.ESRepoRow;
import io.memoria.atom.core.eventsourcing.*;
import io.vavr.collection.List;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.MethodOrderer.OrderAnnotation;

import java.util.concurrent.atomic.AtomicInteger;

import static io.memoria.atom.active.eventsourcing.cassandra.TestUtils.KEYSPACE;

@TestMethodOrder(OrderAnnotation.class)
class CassandraESRepoTest {
  private static final String TOPIC = CassandraESRepoTest.class.getSimpleName() + "_events";
  private static final String STATE_ID = StateId.randomUUID().value();
  private static final CqlSession session = TestUtils.CqlSession();
  private static final CassandraESRepoAdmin admin = new CassandraESRepoAdmin(session);
  private static final CassandraESRepo repo = new CassandraESRepo(KEYSPACE, session);
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
    var rows = List.range(0, COUNT)
                   .zipWithIndex()
                   .map(tup -> repo.append(new ESRepoRow(TOPIC, STATE_ID, tup._2, tup._1 + "")));
    AtomicInteger idx = new AtomicInteger(0);
    // Then
    rows.forEach(i -> Assertions.assertEquals(i.get().seqId(), idx.getAndIncrement()));
  }

  @Test
  @Order(4)
  void get() {
    // When
    var count = repo.getAll(TOPIC, STATE_ID).count();
    // Then
    assert count == COUNT;
  }

  @BeforeAll
  static void beforeAll() {
    System.out.println("QueryClientTest: StateId:" + STATE_ID);
  }

  record UserEvent(EventId eventId, StateId stateId) implements Event {

    @Override
    public CommandId commandId() {
      return CommandId.of(0);
    }

    @Override
    public long timestamp() {
      return 0;
    }
  }
}
