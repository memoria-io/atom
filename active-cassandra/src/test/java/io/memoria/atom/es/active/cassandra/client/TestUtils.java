package io.memoria.atom.es.active.cassandra.client;

public class TestUtils {

  public static final String KEYSPACE = "eventsourcing";

  public static ClientConfig getClientConfig() {
    return new ClientConfig("datacenter1", "localhost", 9042);
  }

  private TestUtils() {}
}
