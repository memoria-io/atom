package io.memoria.atom.testsuite.eventsourcing.banking;

import io.memoria.atom.testsuite.eventsourcing.banking.command.Debit;
import io.memoria.atom.testsuite.eventsourcing.banking.event.DebitRejected;
import io.memoria.atom.testsuite.eventsourcing.banking.event.Debited;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import static io.memoria.atom.testsuite.eventsourcing.banking.TestUtils.*;
import static org.assertj.core.api.Assertions.assertThat;

class AccountDeciderTest {

  @ParameterizedTest
  @ValueSource(ints = {300, 500, 600})
  void debit(int debitAmount) {
    // Given
    int balance = 500;
    var openAccount = createOpenAccount(balance);
    var debit = new Debit(aliceCommandMeta, bobCommandMeta.stateId(), debitAmount);

    // When
    var event = decider.apply(openAccount, debit).get();

    // Then
    assertThat(event.accountId()).isEqualTo(aliceCommandMeta.stateId());
    if (debitAmount < balance) {
      assertThat(event).isInstanceOf(Debited.class);
    } else {
      assertThat(event).isInstanceOf(DebitRejected.class);
    }
  }
}
