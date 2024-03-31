package io.memoria.atom.eventsourcing.rule;

import io.memoria.atom.core.id.Id;
import io.memoria.atom.eventsourcing.ESException;
import io.memoria.atom.eventsourcing.command.Command;
import io.memoria.atom.eventsourcing.command.CommandId;
import io.memoria.atom.eventsourcing.command.CommandMeta;
import io.memoria.atom.eventsourcing.command.exceptions.InvalidEvolutionCommand;
import io.memoria.atom.eventsourcing.command.exceptions.UnknownCommand;
import io.memoria.atom.eventsourcing.event.Event;
import io.memoria.atom.eventsourcing.state.State;
import io.memoria.atom.eventsourcing.state.StateId;
import io.memoria.atom.eventsourcing.state.StateMeta;
import io.memoria.atom.eventsourcing.state.exceptions.UnknownState;
import org.junit.jupiter.api.Test;

import java.util.function.Supplier;

import static org.assertj.core.api.Assertions.assertThat;

class DeciderTest {
  private final Decider decider = new SomeDecider(() -> Id.of(0), () -> 0L);

  @Test
  void applyCreation() {
    // Given
    var createState = new CreateState(new CommandMeta(CommandId.of(0), StateId.of(0)));

    // When
    var event = decider.apply(createState);

    // Then
    assertThat(event).isInstanceOf(StateCreated.class);
    assertThat(event.version()).isEqualTo(0);
  }

  @Test
  void applyEvolution() throws ESException {
    // Given
    var someState = new SomeState(new StateMeta(StateId.of(0)));
    var changeState = new ChangeState(new CommandMeta(CommandId.of(0), StateId.of(0)));

    // When
    var event = decider.apply(someState, changeState);

    // Then
    assertThat(event).isInstanceOf(StateChanged.class);
    assertThat(event.version()).isEqualTo(1);
  }

  private record SomeDecider(Supplier<Id> idSupplier, Supplier<Long> timeSupplier) implements Decider {
    @Override
    public Event apply(Command c) {
      if (c instanceof CreateState createState) {
        return new StateCreated(eventMeta(createState));
      } else {
        throw UnknownCommand.of(c);
      }
    }

    @Override
    public Event apply(State state, Command command) throws ESException {
      if (state instanceof SomeState someState) {
        return switch (command) {
          case CreateState createState -> throw InvalidEvolutionCommand.of(someState, createState);
          case ChangeState changeState -> new StateChanged(eventMeta(someState, changeState));
          default -> throw UnknownCommand.of(command);
        };
      } else {
        throw UnknownState.of(state);
      }
    }
  }
}
