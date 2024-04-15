package io.memoria.atom.eventsourcing.data;

import io.memoria.atom.core.id.Id;
import io.memoria.atom.eventsourcing.aggregate.Decider;
import io.memoria.atom.eventsourcing.command.Command;
import io.memoria.atom.eventsourcing.command.exceptions.CommandException;
import io.memoria.atom.eventsourcing.command.exceptions.InvalidEvolutionCommand;
import io.memoria.atom.eventsourcing.command.exceptions.UnknownCommand;
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
      throw UnknownCommand.of(command);
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

  private Event decide(Command command, SomeState someState) throws InvalidEvolutionCommand {
    return switch (command) {
      case CreateState createState -> throw InvalidEvolutionCommand.of(someState, createState);
      case ChangeState changeState -> new StateChanged(eventMeta(someState, changeState));
      default -> throw UnknownCommand.of(command);
    };
  }
}
