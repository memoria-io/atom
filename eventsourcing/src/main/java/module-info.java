module io.memoria.atom.eventsourcing {
  requires io.memoria.atom.core;
  requires org.slf4j;
  exports com.marmoush.javakit.eventsourcing.aggregate;
  exports com.marmoush.javakit.eventsourcing.aggregate.store;
  exports com.marmoush.javakit.eventsourcing.command;
  exports com.marmoush.javakit.eventsourcing.command.exceptions;
  exports com.marmoush.javakit.eventsourcing.event;
  exports com.marmoush.javakit.eventsourcing.event.repo;
  exports com.marmoush.javakit.eventsourcing.event.exceptions;
  exports com.marmoush.javakit.eventsourcing.saga;
  exports com.marmoush.javakit.eventsourcing.state;
  exports com.marmoush.javakit.eventsourcing.state.exceptions;
}