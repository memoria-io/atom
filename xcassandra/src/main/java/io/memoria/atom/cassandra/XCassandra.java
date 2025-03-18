package io.memoria.atom.cassandra;

import com.datastax.oss.driver.api.core.ConsistencyLevel;
import com.datastax.oss.driver.api.core.CqlSession;
import com.datastax.oss.driver.api.core.cql.SimpleStatement;
import com.datastax.oss.driver.api.querybuilder.QueryBuilder;
import com.datastax.oss.driver.api.querybuilder.SchemaBuilder;
import io.memoria.atom.cassandra.eventsourcing.CassandraEventRepo;
import io.memoria.atom.core.text.TextTransformer;
import io.memoria.atom.eventsourcing.event.repo.EventRepo;

public class XCassandra {

  private XCassandra() {}

  public static SimpleStatement createKeyspace(String keyspace, int replication) {
    return SchemaBuilder.createKeyspace(keyspace).ifNotExists().withSimpleStrategy(replication).build();
  }

  public static SimpleStatement truncate(String keyspace, String table) {
    return QueryBuilder.truncate(keyspace, table).build();
  }

  public static EventRepo cassandraEventRepo(CqlSession session,
                                             ConsistencyLevel writeConsistency,
                                             ConsistencyLevel readConsistency,
                                             String keyspace,
                                             String table,
                                             TextTransformer transformer) {
    return new CassandraEventRepo(session, writeConsistency, readConsistency, keyspace, table, transformer);
  }

  public static EventRepo cassandraEventRepo(CqlSession session,
                                             String keyspace,
                                             String table,
                                             TextTransformer transformer) {
    return new CassandraEventRepo(session, keyspace, table, transformer);
  }
}
