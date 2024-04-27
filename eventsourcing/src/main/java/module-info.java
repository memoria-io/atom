module io.memoria.atom.eventsourcing {
  exports io.memoria.atom.eventsourcing.command;
  exports io.memoria.atom.eventsourcing.command.exceptions;
  exports io.memoria.atom.eventsourcing.event;
  exports io.memoria.atom.eventsourcing.event.exceptions;
  exports io.memoria.atom.eventsourcing.event.repo;
  exports io.memoria.atom.eventsourcing.state;
  exports io.memoria.atom.eventsourcing.state.exceptions;
  requires io.memoria.atom.core;
  requires org.slf4j;
}