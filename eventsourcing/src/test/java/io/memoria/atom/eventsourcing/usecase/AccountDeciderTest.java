package io.memoria.atom.eventsourcing.usecase;

import io.memoria.atom.eventsourcing.usecase.domain.event.DebitRejected;
import io.memoria.atom.eventsourcing.command.CommandId;
import io.memoria.atom.eventsourcing.command.CommandMeta;
import io.memoria.atom.eventsourcing.command.exceptions.CommandException;
import io.memoria.atom.eventsourcing.state.StateMeta;
import io.memoria.atom.eventsourcing.usecase.domain.command.Debit;
import io.memoria.atom.eventsourcing.usecase.domain.event.AccountEvent;
import io.memoria.atom.eventsourcing.usecase.domain.event.Debited;
import io.memoria.atom.eventsourcing.usecase.domain.state.OpenAccount;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import static java.util.UUID.randomUUID;
import static org.assertj.core.api.Assertions.assertThat;

class AccountDeciderTest {

  @ParameterizedTest
  @ValueSource(ints = {300, 500, 600})
  void debit(int debitAmount) throws CommandException {
    // Given
    int balance = 500;
    var openAccount = new OpenAccount(new StateMeta(TestData.aliceId), TestData.alice, balance);
    var debit = new Debit(new CommandMeta(CommandId.of(randomUUID()), TestData.aliceId), TestData.bobId, debitAmount);

    // When
    var event = TestData.decider.decide(openAccount, debit);
    var accountEvent = (AccountEvent) event;

    // Then
    assertThat(accountEvent.accountId()).isEqualTo(TestData.aliceId);
    if (debitAmount < balance) {
      assertThat(event).isInstanceOf(Debited.class);
    } else {
      assertThat(event).isInstanceOf(DebitRejected.class);
    }
  }
}
