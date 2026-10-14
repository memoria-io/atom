package com.marmoush.atom.eventsourcing.saga;

import com.marmoush.atom.eventsourcing.command.Command;
import com.marmoush.atom.eventsourcing.command.CommandPublisher;
import com.marmoush.atom.eventsourcing.event.Event;

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
