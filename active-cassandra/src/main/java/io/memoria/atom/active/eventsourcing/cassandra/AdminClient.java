package io.memoria.atom.active.eventsourcing.cassandra;

import com.datastax.oss.driver.api.core.CqlSession;

public class AdminClient {
  private final CqlSession cqlSession;

  public AdminClient(CqlSession cqlSession) {
    this.cqlSession = cqlSession;
  }

  public boolean createKeyspace(String keyspace, int replication) {
    var st = EventRowSts.createEventsKeyspace(keyspace, replication);
    return cqlSession.execute(st).wasApplied();
  }

  public boolean createEventsTable(String keyspace, String table) {
    var st = EventRowSts.createEventsTable(keyspace, table);
    return cqlSession.execute(st).wasApplied();
  }
}
