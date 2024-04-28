package io.memoria.atom.cassandra;

import com.datastax.oss.driver.api.core.CqlSession;
import com.datastax.oss.driver.api.core.cql.ResultSet;
import com.datastax.oss.driver.api.core.cql.Row;
import com.datastax.oss.driver.api.core.type.DataTypes;
import com.datastax.oss.driver.api.querybuilder.SchemaBuilder;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.MethodOrderer.OrderAnnotation;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;

import java.util.Objects;

@TestMethodOrder(value = OrderAnnotation.class)
class XCassandraIT {
  private static final String KEYSPACE = "some_space";
  private static final CqlSession session = Infra.cqlSession();

  @BeforeAll
  static void beforeAll() {
    // Check connection
    ResultSet rs = session.execute("select release_version from system.local");
    Row row = rs.one();
    var version = Objects.requireNonNull(row).getString("release_version");
    assert version != null && !version.isEmpty();
  }

  @Test
  @Order(0)
  void createKeyspace() {
    // Given
    var st = XCassandra.createKeyspace(KEYSPACE, 1);

    // Then
    Assertions.assertThatNoException().isThrownBy(() -> session.execute(st).wasApplied());
  }

  @Test
  @Order(1)
  void truncateTable() {
    // Given
    String tableName = "SOME_TABLE";
    var creationSt = SchemaBuilder.createTable(KEYSPACE, tableName)
                                  .ifNotExists()
                                  .withPartitionKey("PARTITION_KEY_COL", DataTypes.TEXT)
                                  .build();
    // Then
    Assertions.assertThatNoException().isThrownBy(() -> session.execute(creationSt).wasApplied());

    // And
    var truncateSt = XCassandra.truncate(KEYSPACE, tableName);
    Assertions.assertThatNoException().isThrownBy(() -> session.execute(truncateSt).wasApplied());
  }
}
