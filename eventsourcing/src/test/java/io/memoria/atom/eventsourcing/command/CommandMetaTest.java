package io.memoria.atom.eventsourcing.command;

import io.memoria.atom.eventsourcing.state.StateId;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;

class CommandMetaTest {
  private final StateId stateId = StateId.of(0);

  @Test
  void init() {
    // When
    var commandMeta = new CommandMeta(CommandId.of(0), stateId, 0);

    // Then
    Assertions.assertThat(commandMeta.shardKey()).isEqualTo(stateId);
  }

  @Test
  void nullSagaSource() {
    // Given
    var meta = new CommandMeta(CommandId.of(0), stateId, 0, null);

    // Then
    Assertions.assertThat(meta.sagaSource()).isEmpty();
  }
}
