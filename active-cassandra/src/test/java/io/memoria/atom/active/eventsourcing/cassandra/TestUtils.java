package io.memoria.atom.active.eventsourcing.cassandra;

import com.datastax.oss.driver.api.core.CqlSession;
import com.datastax.oss.driver.api.core.CqlSessionBuilder;

import java.net.InetSocketAddress;

public class TestUtils {

  public static final String KEYSPACE = "eventsourcing";

  private TestUtils() {}

  public static CqlSession CqlSession() {
    return session("datacenter1", "localhost", 9042).build();
  }

  public static CqlSessionBuilder session(String datacenter, String ip, int port) {
    var sock = InetSocketAddress.createUnresolved(ip, port);
    return CqlSession.builder().withLocalDatacenter(datacenter).addContactPoint(sock);
  }

  public static boolean createKeyspace(CqlSession cqlSession, String keyspace, int replication) {
    var st = EventRowSts.createEventsKeyspace(keyspace, replication);
    return cqlSession.execute(st).wasApplied();
  }

  public static boolean createEventsTable(CqlSession cqlSession, String keyspace, String table) {
    var st = EventRowSts.createEventsTable(keyspace, table);
    return cqlSession.execute(st).wasApplied();
  }
}
