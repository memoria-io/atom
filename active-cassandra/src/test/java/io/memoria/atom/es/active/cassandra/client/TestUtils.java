package io.memoria.atom.es.active.cassandra.client;

public class TestUtils {

  public static final String KEYSPACE = "eventsourcing";

  private TestUtils() {}

  public static ClientConfig getClientConfig() {
    return new ClientConfig("datacenter1", "localhost", 9042);
  }
}
