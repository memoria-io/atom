package io.memoria.atom.active.cassandra;

import com.datastax.oss.driver.api.core.CqlSession;
import com.datastax.oss.driver.api.core.CqlSessionBuilder;

import java.net.InetSocketAddress;

class SessionUtils {
  public static CqlSessionBuilder session(ClientConfig config) {
    var sock = InetSocketAddress.createUnresolved(config.ip(), config.port());
    return CqlSession.builder().withLocalDatacenter(config.datacenter()).addContactPoint(sock);
  }
}
