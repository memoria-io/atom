package io.memoria.atom.eventsourcing.rule;

import io.memoria.atom.core.id.Id;
import io.memoria.atom.eventsourcing.ESException;
import io.memoria.atom.eventsourcing.command.Command;
import io.memoria.atom.eventsourcing.command.CommandId;
import io.memoria.atom.eventsourcing.command.CommandMeta;
import io.memoria.atom.eventsourcing.command.exceptions.InvalidEvolutionCommand;
import io.memoria.atom.eventsourcing.command.exceptions.MismatchingCommandState;
import io.memoria.atom.eventsourcing.command.exceptions.UnknownCommand;
import io.memoria.atom.eventsourcing.event.Event;
import io.memoria.atom.eventsourcing.event.EventMeta;
import io.memoria.atom.eventsourcing.state.State;
import io.memoria.atom.eventsourcing.state.StateId;
import io.memoria.atom.eventsourcing.state.StateMeta;
import io.memoria.atom.eventsourcing.state.exceptions.UnknownState;
import org.junit.jupiter.api.Test;

import java.util.function.Supplier;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

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

  @Test
  void applyEvolutionFail() {
    // Given
    var someState = new SomeState(new StateMeta(StateId.of("stateId")));
    var changeState = new ChangeState(new CommandMeta(CommandId.of(0), StateId.of("differentStateId")));

    // then
    assertThatThrownBy(() -> decider.apply(someState, changeState)).isInstanceOf(MismatchingCommandState.class);
  }

  private record SomeDecider(Supplier<Id> idSupplier, Supplier<Long> timeSupplier) implements Decider {
    @Override
    public Event createBy(Command command, EventMeta eventMeta) {
      if (command instanceof CreateState) {
        return new StateCreated(eventMeta);
      } else {
        throw UnknownCommand.of(command);
      }
    }

    @Override
    public Event decide(State state, Command command, EventMeta eventMeta) throws ESException {
      if (state instanceof SomeState someState) {
        return handle(command, someState);
      } else {
        throw UnknownState.of(state);
      }
    }

    private Event handle(Command command, SomeState someState) throws InvalidEvolutionCommand {
      return switch (command) {
        case CreateState createState -> throw InvalidEvolutionCommand.of(someState, createState);
        case ChangeState changeState -> new StateChanged(eventMeta(someState, changeState));
        default -> throw UnknownCommand.of(command);
      };
    }
  }
}
