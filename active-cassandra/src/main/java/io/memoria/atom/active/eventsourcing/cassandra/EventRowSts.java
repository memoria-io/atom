package io.memoria.atom.active.eventsourcing.cassandra;

import com.datastax.oss.driver.api.core.cql.SimpleStatement;
import com.datastax.oss.driver.api.core.metadata.schema.ClusteringOrder;
import com.datastax.oss.driver.api.querybuilder.QueryBuilder;
import com.datastax.oss.driver.api.querybuilder.SchemaBuilder;

import static com.datastax.oss.driver.api.querybuilder.QueryBuilder.literal;

class EventRowSts {

  private EventRowSts() {}

  public static SimpleStatement push(String keyspace, String table, EventRow row) {
    return QueryBuilder.insertInto(keyspace, table)
                       .value(EventRow.stateIdCol, literal(row.stateId()))
                       .value(EventRow.seqCol, literal(row.seqId()))
                       .value(EventRow.eventCol, literal(row.value()))
                       .value(EventRow.createdAtCol, literal(row.createdAt()))
                       .ifNotExists()
                       .build();
  }

  public static SimpleStatement getLastSeqId(String keyspace, String table, String stateId) {
    return QueryBuilder.selectFrom(keyspace, table)
                       .all()
                       .orderBy(EventRow.seqCol, ClusteringOrder.DESC)
                       .limit(1)
                       .whereColumn(EventRow.stateIdCol)
                       .isEqualTo(literal(stateId))
                       .build();
  }

  public static SimpleStatement get(String keyspace, String table, String stateId) {
    return QueryBuilder.selectFrom(keyspace, table)
                       .all()
                       .whereColumn(EventRow.stateIdCol)
                       .isEqualTo(literal(stateId))
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
                        .withPartitionKey(EventRow.stateIdCol, EventRow.stateIdColType)
                        .withClusteringColumn(EventRow.seqCol, EventRow.seqColType)
                        .withColumn(EventRow.eventCol, EventRow.eventColType)
                        .withColumn(EventRow.createdAtCol, EventRow.createAtColType)
                        .build();
  }
}
