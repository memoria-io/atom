package com.marmoush.atom.eventsourcing.aggregate;

import com.marmoush.atom.core.id.Ids;
import com.marmoush.atom.eventsourcing.command.CommandIds;
import com.marmoush.atom.eventsourcing.command.CommandMeta;
import com.marmoush.atom.eventsourcing.command.exceptions.CommandException;
import com.marmoush.atom.eventsourcing.command.exceptions.MismatchingCommandState;
import com.marmoush.atom.eventsourcing.state.StateIds;
import com.marmoush.atom.eventsourcing.state.StateMeta;
import com.marmoush.atom.eventsourcing.usecase.simple.ChangeState;
import com.marmoush.atom.eventsourcing.usecase.simple.CreateState;
import com.marmoush.atom.eventsourcing.usecase.simple.SimpleDecider;
import com.marmoush.atom.eventsourcing.usecase.simple.SimpleState;
import com.marmoush.atom.eventsourcing.usecase.simple.StateChanged;
import com.marmoush.atom.eventsourcing.usecase.simple.StateCreated;
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
