package io.memoria.atom.eventsourcing.aggregate;

import io.memoria.atom.core.id.Ids;
import io.memoria.atom.eventsourcing.command.CommandIds;
import io.memoria.atom.eventsourcing.command.CommandMeta;
import io.memoria.atom.eventsourcing.command.exceptions.CommandException;
import io.memoria.atom.eventsourcing.command.exceptions.MismatchingCommandState;
import io.memoria.atom.eventsourcing.state.StateIds;
import io.memoria.atom.eventsourcing.state.StateMeta;
import io.memoria.atom.eventsourcing.usecase.simple.ChangeState;
import io.memoria.atom.eventsourcing.usecase.simple.CreateState;
import io.memoria.atom.eventsourcing.usecase.simple.SimpleDecider;
import io.memoria.atom.eventsourcing.usecase.simple.SimpleState;
import io.memoria.atom.eventsourcing.usecase.simple.StateChanged;
import io.memoria.atom.eventsourcing.usecase.simple.StateCreated;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class DeciderTest {
  private final Decider decider = new SimpleDecider(() -> Ids.of(0), () -> 0L);

  @Test
  void decideCreation() throws CommandException {
    // Given
    var createState = new CreateState(new CommandMeta(CommandIds.of(0), StateIds.of(0)));

    // When
    var event = decider.decide(createState);

    // Then
    assertThat(event).isInstanceOf(StateCreated.class);
    assertThat(event.version()).isZero();
  }

  @Test
  void decideEvolution() throws CommandException {
    // Given
    var someState = new SimpleState(new StateMeta(StateIds.of(0)));
    var changeState = new ChangeState(new CommandMeta(CommandIds.of(0), StateIds.of(0)));

    // When
    var event = decider.decide(someState, changeState);

    // Then
    assertThat(event).isInstanceOf(StateChanged.class);
    assertThat(event.version()).isOne();
  }

  @Test
  void decideEvolutionFail() {
    // Given
    var someState = new SimpleState(new StateMeta(StateIds.of("stateId")));
    var changeState = new ChangeState(new CommandMeta(CommandIds.of(0), StateIds.of("differentStateId")));

    // then
    assertThatThrownBy(() -> decider.decide(someState, changeState)).isInstanceOf(MismatchingCommandState.class);
  }
}
