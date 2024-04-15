package io.memoria.atom.tests.eventsourcing;

import io.memoria.atom.eventsourcing.command.CommandId;
import io.memoria.atom.eventsourcing.event.EventId;
import io.memoria.atom.eventsourcing.event.EventMeta;
import io.memoria.atom.eventsourcing.state.StateMeta;
import io.memoria.atom.tests.eventsourcing.event.Debited;
import io.memoria.atom.tests.eventsourcing.state.OpenAccount;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;

import static io.memoria.atom.tests.eventsourcing.TestData.alice;
import static io.memoria.atom.tests.eventsourcing.TestData.aliceId;
import static io.memoria.atom.tests.eventsourcing.TestData.bobId;
import static io.memoria.atom.tests.eventsourcing.TestData.evolver;
import static java.util.UUID.randomUUID;

class AccountEvolverTest {
  @Test
  void evolve() {
    // Given
    var openAccount = new OpenAccount(new StateMeta(aliceId), alice, 500);
    var debited = new Debited(new EventMeta(EventId.of(randomUUID()), 1, aliceId, CommandId.of(randomUUID())),
                              bobId,
                              300);

    // When
    var acc = (OpenAccount) evolver.apply(openAccount, debited);

    // Then
    Assertions.assertThat(acc.balance()).isEqualTo(200);
  }
}
