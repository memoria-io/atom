package io.memoria.atom.eventsourcing.event;

import io.memoria.atom.eventsourcing.command.CommandIds;
import io.memoria.atom.eventsourcing.state.StateId;
import io.memoria.atom.eventsourcing.state.StateIds;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;

class EventMetaTest {
  private final StateId stateId = StateIds.of(0);

  @Test
  void init() {
    // When
    var eventMeta = new EventMeta(EventIds.of(0), 0, stateId, CommandIds.of(0), 0);

    // Then
    Assertions.assertThat(eventMeta.pKey()).isEqualTo(stateId);
  }

  @Test
  void wrongVersion() {
    Assertions.assertThatIllegalArgumentException()
              .isThrownBy(() -> new EventMeta(EventIds.of(0), -1, stateId, CommandIds.of(0), 0));
  }

  @Test
  void equality() {
    // Given
    var eventMeta1 = new EventMeta(EventIds.of(0), 0, stateId, CommandIds.of(0), 0);
    var eventMeta2 = new EventMeta(EventIds.of(0), 0, stateId, CommandIds.of(0), 0);

    // Then
    Assertions.assertThat(eventMeta1).isEqualTo(eventMeta2);
  }

  @Test
  void nullSagaSource() {
    // Given
    var meta = new EventMeta(EventIds.of(0), 0, stateId, CommandIds.of(0), 0, null);

    // Then
    Assertions.assertThat(meta.sagaSource()).isEmpty();
  }
}
