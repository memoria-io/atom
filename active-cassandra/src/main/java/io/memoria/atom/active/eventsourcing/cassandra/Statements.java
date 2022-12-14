package io.memoria.atom.active.eventsourcing.cassandra;

import com.datastax.oss.driver.api.core.cql.SimpleStatement;
import com.datastax.oss.driver.api.core.metadata.schema.ClusteringOrder;
import com.datastax.oss.driver.api.querybuilder.QueryBuilder;
import com.datastax.oss.driver.api.querybuilder.SchemaBuilder;

import static com.datastax.oss.driver.api.querybuilder.QueryBuilder.literal;

class Statements {

  private Statements() {}

  public static SimpleStatement push(String keyspace, String table, CassandraRow row) {
    return QueryBuilder.insertInto(keyspace, table)
                       .value(CassandraRow.stateIdCol, literal(row.stateId()))
                       .value(CassandraRow.seqCol, literal(row.seqId()))
                       .value(CassandraRow.payloadCol, literal(row.payload()))
                       .value(CassandraRow.createdAtCol, literal(row.createdAt()))
                       .ifNotExists()
                       .build();
  }

  public static SimpleStatement getLastSeqId(String keyspace, String table, String stateId) {
    return QueryBuilder.selectFrom(keyspace, table)
                       .all()
                       .orderBy(CassandraRow.seqCol, ClusteringOrder.DESC)
                       .limit(1)
                       .whereColumn(CassandraRow.stateIdCol)
                       .isEqualTo(literal(stateId))
                       .build();
  }

  public static SimpleStatement get(String keyspace, String table, String stateId, int startIdx) {
    return QueryBuilder.selectFrom(keyspace, table)
                       .all()
                       .whereColumn(CassandraRow.stateIdCol)
                       .isEqualTo(literal(stateId))
                       .whereColumn(CassandraRow.seqCol)
                       .isGreaterThanOrEqualTo(literal(startIdx))
                       .build();
  }

  public static SimpleStatement createEventsKeyspace(String keyspace, int replication) {
    return SchemaBuilder.createKeyspace(keyspace).ifNotExists().withSimpleStrategy(replication).build();
  }

  public static SimpleStatement truncate(String keyspace, String table) {
    return QueryBuilder.truncate(keyspace, table).build();
  }

  public static SimpleStatement createEventsTable(String keyspace, String table) {
    return SchemaBuilder.createTable(keyspace, table)
                        .ifNotExists()
                        .withPartitionKey(CassandraRow.stateIdCol, CassandraRow.stateIdColType)
                        .withClusteringColumn(CassandraRow.seqCol, CassandraRow.seqColType)
                        .withColumn(CassandraRow.payloadCol, CassandraRow.payloadColType)
                        .withColumn(CassandraRow.createdAtCol, CassandraRow.createAtColType)
                        .build();
  }
}
