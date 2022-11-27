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
}
