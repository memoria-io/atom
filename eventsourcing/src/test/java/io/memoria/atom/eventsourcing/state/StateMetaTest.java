package io.memoria.atom.eventsourcing.state;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;

class StateMetaTest {
  private final StateId stateId = StateId.of(0);

  @Test
  void init() {
    // When
    var stateMeta1 = new StateMeta(stateId, 0);
    var stateMeta2 = new StateMeta(stateId);

    // Then
    Assertions.assertThat(stateMeta1.shardKey()).isEqualTo(stateId);
    Assertions.assertThat(stateMeta1).isEqualTo(stateMeta2);
    Assertions.assertThat(stateMeta1.incrementVersion()).isEqualTo(stateMeta2.incrementVersion());
    Assertions.assertThat(stateMeta1.incrementVersion().version()).isEqualTo(1);
  }

  @Test
  void stateVersion() {
    Assertions.assertThatIllegalArgumentException().isThrownBy(() -> new StateMeta(stateId, -1));
  }
}
