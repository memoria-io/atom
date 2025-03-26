module io.memoria.atom.eventsourcing {
  requires io.memoria.atom.core;
  requires org.slf4j;
  requires cache.api;
  exports io.memoria.atom.eventsourcing.aggregate;
  exports io.memoria.atom.eventsourcing.aggregate.store;
  exports io.memoria.atom.eventsourcing.command;
  exports io.memoria.atom.eventsourcing.command.exceptions;
  exports io.memoria.atom.eventsourcing.event;
  exports io.memoria.atom.eventsourcing.event.repo;
  exports io.memoria.atom.eventsourcing.event.exceptions;
  exports io.memoria.atom.eventsourcing.saga;
  exports io.memoria.atom.eventsourcing.state;
  exports io.memoria.atom.eventsourcing.state.exceptions;
}