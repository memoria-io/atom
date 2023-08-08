package io.memoria.atom.testsuite.eventsourcing.banking;

import io.memoria.atom.eventsourcing.CommandMeta;
import io.memoria.atom.eventsourcing.StateMeta;
import io.memoria.atom.testsuite.eventsourcing.banking.command.Debit;
import io.memoria.atom.testsuite.eventsourcing.banking.event.DebitRejected;
import io.memoria.atom.testsuite.eventsourcing.banking.event.Debited;
import io.memoria.atom.testsuite.eventsourcing.banking.state.OpenAccount;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import static io.memoria.atom.testsuite.eventsourcing.banking.TestUtils.alice;
import static io.memoria.atom.testsuite.eventsourcing.banking.TestUtils.aliceId;
import static io.memoria.atom.testsuite.eventsourcing.banking.TestUtils.bobId;
import static io.memoria.atom.testsuite.eventsourcing.banking.TestUtils.decider;
import static org.assertj.core.api.Assertions.assertThat;

class AccountDeciderTest {

  @ParameterizedTest
  @ValueSource(ints = {300, 500, 600})
  void debit(int debitAmount) {
    // Given
    int balance = 500;
    var openAccount = new OpenAccount(new StateMeta(aliceId), alice, balance);
    var debit = new Debit(new CommandMeta(aliceId), bobId, debitAmount);

    // When
    var event = decider.apply(openAccount, debit).get();

    // Then
    assertThat(event.accountId()).isEqualTo(aliceId);
    if (debitAmount < balance) {
      assertThat(event).isInstanceOf(Debited.class);
    } else {
      assertThat(event).isInstanceOf(DebitRejected.class);
    }
  }
}
