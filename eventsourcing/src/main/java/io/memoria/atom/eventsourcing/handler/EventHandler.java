package io.memoria.atom.eventsourcing.handler;

import io.memoria.atom.eventsourcing.command.Command;
import io.memoria.atom.eventsourcing.command.CommandPublisher;
import io.memoria.atom.eventsourcing.event.Event;
import io.memoria.atom.eventsourcing.rule.Saga;

import java.util.Optional;
import java.util.function.Function;

public class EventHandler implements Function<Event, Optional<Command>> {
  private final Saga saga;
  private final CommandPublisher commandPublisher;

  public EventHandler(Saga saga, CommandPublisher commandPublisher) {
    this.saga = saga;
    this.commandPublisher = commandPublisher;
  }

  public Optional<Command> apply(Event event) {
    Optional<Command> result = saga.apply(event);
    result.ifPresent(commandPublisher::publish);
    return result;
  }
}
