package io.memoria.atom.active.cassandra;

import io.memoria.atom.core.eventsourcing.StateId;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.MethodOrderer.OrderAnnotation;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;

import java.util.stream.IntStream;

import static io.memoria.atom.active.cassandra.TestUtils.KEYSPACE;
import static io.memoria.atom.active.cassandra.TestUtils.getClientConfig;

@TestMethodOrder(OrderAnnotation.class)
class QueryClientTest {
  private static final String TABLE = QueryClientTest.class.getSimpleName() + "_events";
  private static final StateId STATE_ID = StateId.randomUUID();
  private static final AdminClient admin = new AdminClient(getClientConfig());
  private static final QueryClient client = new QueryClient(getClientConfig());
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
    var isCreated = admin.createEventsTable(KEYSPACE, TABLE);
    // Then
    assert isCreated;
  }

  @Test
  @Order(3)
  void push() {
    // When
    var rows = IntStream.range(0, COUNT).mapToObj(i -> client.push(KEYSPACE, TABLE, STATE_ID, "someEvent_" + i));
    // Then
    rows.forEach(Assertions::assertTrue);
  }

  @Test
  @Order(4)
  void get() {
    // When
    var count = client.get(TestUtils.KEYSPACE, TABLE, STATE_ID).count();
    // Then
    assert count == COUNT;
  }

  @BeforeAll
  static void beforeAll() {
    System.out.println("QueryClientTest: StateId:" + STATE_ID);
  }
}
