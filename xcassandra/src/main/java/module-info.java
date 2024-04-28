module io.memoria.atom.cassandra {
  requires io.memoria.atom.core;
  requires io.memoria.atom.eventsourcing;
  requires java.driver.core;
  requires java.driver.query.builder;
  exports io.memoria.atom.cassandra;
}