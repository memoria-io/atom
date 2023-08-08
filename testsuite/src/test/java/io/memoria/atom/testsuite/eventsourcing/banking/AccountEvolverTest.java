package io.memoria.atom.testsuite.eventsourcing.banking;

import io.memoria.atom.testsuite.eventsourcing.banking.event.Debited;
import io.memoria.atom.testsuite.eventsourcing.banking.state.OpenAccount;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;

import static io.memoria.atom.testsuite.eventsourcing.banking.TestUtils.aliceEventMeta;
import static io.memoria.atom.testsuite.eventsourcing.banking.TestUtils.bobCommandMeta;
import static io.memoria.atom.testsuite.eventsourcing.banking.TestUtils.createOpenAccount;
import static io.memoria.atom.testsuite.eventsourcing.banking.TestUtils.evolver;

class AccountEvolverTest {
  @Test
  void evolve() {
    // Given
    var openAccount = createOpenAccount(500);
    var debited = new Debited(aliceEventMeta, bobCommandMeta.stateId(), 300);

    // When
    var acc = (OpenAccount) evolver.apply(openAccount, debited);

    // Then
    Assertions.assertThat(acc.balance()).isEqualTo(200);
  }
}
