package io.memoria.atom.active.eventsourcing.cassandra;

import com.datastax.oss.driver.api.core.CqlSession;

public class AdminClient {
  private final CqlSession cqlSession;

  public AdminClient(ClientConfig config) {
    this.cqlSession = SessionUtils.session(config).build();
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
