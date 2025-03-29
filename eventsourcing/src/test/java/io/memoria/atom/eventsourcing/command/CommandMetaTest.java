package io.memoria.atom.eventsourcing.command;

import io.memoria.atom.eventsourcing.state.StateId;
import io.memoria.atom.eventsourcing.state.StateIds;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;

class CommandMetaTest {
  private final StateId stateId = StateIds.of(0);

  @Test
  void init() {
    // Given
    var commandMeta = new CommandMeta(CommandIds.of(0), stateId, 0);

    // Then
    Assertions.assertThat(commandMeta.pKey()).isEqualTo(stateId);
  }

  @Test
  void equality() {
    // Given
    var commandMeta1 = new CommandMeta(CommandIds.of(0), stateId, 0);
    var commandMeta2 = new CommandMeta(CommandIds.of(0), stateId, 0);

    // Then
    Assertions.assertThat(commandMeta1).isEqualTo(commandMeta2);
  }

  @Test
  void nullSagaSource() {
    // Given
    var meta = new CommandMeta(CommandIds.of(0), stateId, 0, null);

    // Then
    Assertions.assertThat(meta.sagaSource()).isEmpty();
  }
}
