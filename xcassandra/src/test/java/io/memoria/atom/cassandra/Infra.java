package io.memoria.atom.cassandra;

import com.datastax.oss.driver.api.core.CqlSession;
import com.datastax.oss.driver.api.core.CqlSessionBuilder;

import java.net.InetSocketAddress;

public class Infra {

  private Infra() {}

  public static CqlSession cqlSession() {
    return session("datacenter1", "127.0.0.1", 9042).build();
  }

  public static CqlSessionBuilder session(String datacenter, String ip, int port) {
    var sock = InetSocketAddress.createUnresolved(ip, port);
    return CqlSession.builder().withLocalDatacenter(datacenter).addContactPoint(sock);
  }
}
