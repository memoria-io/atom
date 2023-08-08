package io.memoria.atom.testsuite.eventsourcing.banking;

import io.memoria.atom.eventsourcing.CommandId;
import io.memoria.atom.eventsourcing.EventMeta;
import io.memoria.atom.eventsourcing.StateMeta;
import io.memoria.atom.testsuite.eventsourcing.banking.event.Debited;
import io.memoria.atom.testsuite.eventsourcing.banking.state.OpenAccount;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;

import static io.memoria.atom.testsuite.eventsourcing.banking.TestUtils.alice;
import static io.memoria.atom.testsuite.eventsourcing.banking.TestUtils.aliceId;
import static io.memoria.atom.testsuite.eventsourcing.banking.TestUtils.bobCommandMeta;
import static io.memoria.atom.testsuite.eventsourcing.banking.TestUtils.evolver;

class AccountEvolverTest {
  @Test
  void evolve() {
    // Given
    var openAccount = new OpenAccount(new StateMeta(aliceId), alice, 500);
    var debited = new Debited(new EventMeta(CommandId.of(), 1, aliceId), bobCommandMeta.stateId(), 300);

    // When
    var acc = (OpenAccount) evolver.apply(openAccount, debited);

    // Then
    Assertions.assertThat(acc.balance()).isEqualTo(200);
  }
}
