package io.memoria.atom.eventsourcing.usecase.banking;

import io.memoria.atom.eventsourcing.command.CommandIds;
import io.memoria.atom.eventsourcing.event.EventIds;
import io.memoria.atom.eventsourcing.event.EventMeta;
import io.memoria.atom.eventsourcing.state.StateId;
import io.memoria.atom.eventsourcing.usecase.banking.command.Credit;
import io.memoria.atom.eventsourcing.usecase.banking.event.Debited;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;

import static java.util.UUID.randomUUID;

class AccountSagaTest {
  @Test
  void evolve() {
    // Given
    StateId debitedAccount = TestData.aliceId;
    StateId creditedAcc = TestData.bobId;

    var sagaEventId = EventIds.of("SomeSagaEventId");
    var commandId = CommandIds.of(randomUUID());
    var eventId = EventIds.of(randomUUID());
    var eventMetaWithSaga = new EventMeta(eventId, 1, debitedAccount, commandId, 0, sagaEventId);

    var debited = new Debited(eventMetaWithSaga, creditedAcc, 300);

    // When
    var command = TestData.saga.react(debited);

    // Then
    Assertions.assertThat(command).isPresent();
    Assertions.assertThat(command.get()).isInstanceOf(Credit.class);
    // And
    var credit = (Credit) command.get();
    Assertions.assertThat(credit.accountId()).isEqualTo(creditedAcc);
    Assertions.assertThat(credit.debitedAcc()).isEqualTo(debitedAccount);
  }
}
