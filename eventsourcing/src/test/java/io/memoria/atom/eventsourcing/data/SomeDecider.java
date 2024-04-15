package io.memoria.atom.eventsourcing.data;

import io.memoria.atom.core.id.Id;
import io.memoria.atom.eventsourcing.aggregate.Decider;
import io.memoria.atom.eventsourcing.command.Command;
import io.memoria.atom.eventsourcing.command.exceptions.CommandException;
import io.memoria.atom.eventsourcing.command.exceptions.InvalidCommand;
import io.memoria.atom.eventsourcing.command.exceptions.UnknownCommandRTE;
import io.memoria.atom.eventsourcing.event.Event;
import io.memoria.atom.eventsourcing.event.EventMeta;
import io.memoria.atom.eventsourcing.state.State;
import io.memoria.atom.eventsourcing.state.exceptions.UnknownState;

import java.util.function.Supplier;

public record SomeDecider(Supplier<Id> idSupplier, Supplier<Long> timeSupplier) implements Decider {
  @Override
  public Event decide(Command command, EventMeta eventMeta) {
    if (command instanceof CreateState) {
      return new StateCreated(eventMeta);
    } else {
      throw UnknownCommandRTE.of(command);
    }
  }

  @Override
  public Event decide(State state, Command command, EventMeta eventMeta) throws CommandException {
    if (state instanceof SomeState someState) {
      return decide(command, someState);
    } else {
      throw UnknownState.of(state);
    }
  }

  private Event decide(Command command, SomeState someState) throws InvalidCommand {
    return switch (command) {
      case CreateState createState -> throw InvalidCommand.ofEvolution(someState, createState);
      case ChangeState changeState -> new StateChanged(eventMeta(someState, changeState));
      default -> throw UnknownCommandRTE.of(command);
    };
  }
}
