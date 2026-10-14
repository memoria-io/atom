package com.marmoush.atom.eventsourcing.usecase.simple;

import com.marmoush.atom.core.id.Id;
import com.marmoush.atom.eventsourcing.aggregate.Decider;
import com.marmoush.atom.eventsourcing.command.Command;
import com.marmoush.atom.eventsourcing.command.exceptions.CommandException;
import com.marmoush.atom.eventsourcing.command.exceptions.InvalidCommand;
import com.marmoush.atom.eventsourcing.command.exceptions.UnknownCommandRTE;
import com.marmoush.atom.eventsourcing.event.Event;
import com.marmoush.atom.eventsourcing.event.EventMeta;
import com.marmoush.atom.eventsourcing.state.State;
import com.marmoush.atom.eventsourcing.state.exceptions.UnknownState;

import java.util.function.Supplier;

public record SimpleDecider(Supplier<Id> idSupplier, Supplier<Long> timeSupplier) implements Decider {
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
    if (state instanceof SimpleState simpleState) {
      return decide(command, simpleState);
    } else {
      throw UnknownState.of(state);
    }
  }

  private Event decide(Command command, SimpleState simpleState) throws InvalidCommand {
    return switch (command) {
      case CreateState createState -> throw InvalidCommand.ofEvolution(simpleState, createState);
      case ChangeState changeState -> new StateChanged(eventMeta(simpleState, changeState));
      default -> throw UnknownCommandRTE.of(command);
    };
  }
}
