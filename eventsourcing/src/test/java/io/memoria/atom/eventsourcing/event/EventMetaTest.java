package io.memoria.atom.eventsourcing.event;

import io.memoria.atom.eventsourcing.command.CommandId;
import io.memoria.atom.eventsourcing.event.EventId;
import io.memoria.atom.eventsourcing.event.EventMeta;
import io.memoria.atom.eventsourcing.state.StateId;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;

public class EventMetaTest {
  private final StateId stateId = StateId.of(0);

  @Test
  void init() {
    // When
    var eventMeta = new EventMeta(EventId.of(0), 0, stateId, CommandId.of(0), 0);

    // Then
    Assertions.assertThat(eventMeta.shardKey()).isEqualTo(stateId);
  }

  @Test
  void nullSagaSource() {
    Assertions.assertThatNullPointerException()
              .isThrownBy(() -> new EventMeta(EventId.of(0), 0, stateId, CommandId.of(0), 0, null));
  }

}
