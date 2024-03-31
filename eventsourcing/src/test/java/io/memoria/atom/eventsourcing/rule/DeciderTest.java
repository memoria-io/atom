package io.memoria.atom.eventsourcing.rule;

import io.memoria.atom.eventsourcing.command.CommandId;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;

class DeciderTest {
  @Test
  void checkToString() {
    Assertions.assertThat(new CommandId("id")).hasToString("id");
  }
}
