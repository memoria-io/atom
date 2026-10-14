package com.marmoush.javakit.eventsourcing.usecase.banking;

import com.marmoush.javakit.eventsourcing.command.CommandIds;
import com.marmoush.javakit.eventsourcing.command.CommandMeta;
import com.marmoush.javakit.eventsourcing.command.exceptions.CommandException;
import com.marmoush.javakit.eventsourcing.state.StateMeta;
import com.marmoush.javakit.eventsourcing.usecase.banking.command.Debit;
import com.marmoush.javakit.eventsourcing.usecase.banking.event.AccountEvent;
import com.marmoush.javakit.eventsourcing.usecase.banking.event.DebitRejected;
import com.marmoush.javakit.eventsourcing.usecase.banking.event.Debited;
import com.marmoush.javakit.eventsourcing.usecase.banking.state.OpenAccount;
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
    var debit = new Debit(new CommandMeta(CommandIds.of(randomUUID()), TestData.aliceId), TestData.bobId, debitAmount);

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
