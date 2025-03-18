package io.memoria.atom.eventsourcing.saga;

import io.memoria.atom.eventsourcing.command.Command;
import io.memoria.atom.eventsourcing.command.CommandPublisher;
import io.memoria.atom.eventsourcing.event.Event;

import java.util.Optional;

public class SagaHandler {
  private final Saga saga;
  private final CommandPublisher commandPublisher;

  public SagaHandler(Saga saga, CommandPublisher commandPublisher) {
    this.saga = saga;
    this.commandPublisher = commandPublisher;
  }

  public Optional<Command> react(Event event) {
    Optional<Command> result = saga.react(event);
    result.ifPresent(commandPublisher::publish);
    return result;
  }
}
