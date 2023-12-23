package io.memoria.atom.testsuite.eventsourcing;

import io.memoria.atom.eventsourcing.CommandId;
import io.memoria.atom.eventsourcing.EventId;
import io.memoria.atom.eventsourcing.EventMeta;
import io.memoria.atom.eventsourcing.StateMeta;
import io.memoria.atom.testsuite.eventsourcing.event.Debited;
import io.memoria.atom.testsuite.eventsourcing.state.OpenAccount;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;

import static io.memoria.atom.testsuite.eventsourcing.TestData.alice;
import static io.memoria.atom.testsuite.eventsourcing.TestData.aliceId;
import static io.memoria.atom.testsuite.eventsourcing.TestData.bobId;
import static io.memoria.atom.testsuite.eventsourcing.TestData.evolver;
import static java.util.UUID.randomUUID;

class AccountEvolverTest {
  @Test
  void evolve() {
    // Given
    var openAccount = new OpenAccount(new StateMeta(aliceId), alice, 500);
    var debited = new Debited(new EventMeta(EventId.of(randomUUID()), CommandId.of(randomUUID()), 1, aliceId),
                              bobId,
                              300);

    // When
    var acc = (OpenAccount) evolver.apply(openAccount, debited);

    // Then
    Assertions.assertThat(acc.balance()).isEqualTo(200);
  }
}
