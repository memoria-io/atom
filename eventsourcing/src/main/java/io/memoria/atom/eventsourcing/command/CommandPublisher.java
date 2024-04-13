package io.memoria.atom.eventsourcing.command;

public interface CommandPublisher {
  void publish(Command command);
}
