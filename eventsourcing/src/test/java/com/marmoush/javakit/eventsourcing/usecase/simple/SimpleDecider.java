package com.marmoush.javakit.eventsourcing.usecase.simple;

import com.marmoush.javakit.core.id.Id;
import com.marmoush.javakit.eventsourcing.aggregate.Decider;
import com.marmoush.javakit.eventsourcing.command.Command;
import com.marmoush.javakit.eventsourcing.command.exceptions.CommandException;
import com.marmoush.javakit.eventsourcing.command.exceptions.InvalidCommand;
import com.marmoush.javakit.eventsourcing.command.exceptions.UnknownCommandRTE;
import com.marmoush.javakit.eventsourcing.event.Event;
import com.marmoush.javakit.eventsourcing.event.EventMeta;
import com.marmoush.javakit.eventsourcing.state.State;
import com.marmoush.javakit.eventsourcing.state.exceptions.UnknownState;

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
