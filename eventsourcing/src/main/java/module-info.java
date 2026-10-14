module io.memoria.atom.eventsourcing {
  requires io.memoria.atom.core;
  requires org.slf4j;
  exports com.marmoush.atom.eventsourcing.aggregate;
  exports com.marmoush.atom.eventsourcing.aggregate.store;
  exports com.marmoush.atom.eventsourcing.command;
  exports com.marmoush.atom.eventsourcing.command.exceptions;
  exports com.marmoush.atom.eventsourcing.event;
  exports com.marmoush.atom.eventsourcing.event.repo;
  exports com.marmoush.atom.eventsourcing.event.exceptions;
  exports com.marmoush.atom.eventsourcing.saga;
  exports com.marmoush.atom.eventsourcing.state;
  exports com.marmoush.atom.eventsourcing.state.exceptions;
}