package com.marmoush.atom.eventsourcing.command;

public interface CommandPublisher {
  void publish(Command command);
}
