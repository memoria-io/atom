package com.marmoush.javakit.eventsourcing.command;

public interface CommandPublisher {
  void publish(Command command);
}
