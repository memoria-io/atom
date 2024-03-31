package io.memoria.atom.testsuite.eventsourcing;

import io.memoria.atom.eventsourcing.command.CommandId;
import io.memoria.atom.eventsourcing.event.EventId;
import io.memoria.atom.eventsourcing.event.EventMeta;
import io.memoria.atom.eventsourcing.state.StateId;
import io.memoria.atom.testsuite.eventsourcing.command.Credit;
import io.memoria.atom.testsuite.eventsourcing.event.Debited;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.Optional;

import static io.memoria.atom.testsuite.eventsourcing.TestData.aliceId;
import static io.memoria.atom.testsuite.eventsourcing.TestData.bobId;
import static io.memoria.atom.testsuite.eventsourcing.TestData.saga;
import static java.util.UUID.randomUUID;

class AccountSagaTest {
  @Test
  void evolve() {
    // Given
    StateId debitedAccount = aliceId;
    StateId creditedAcc = bobId;

    var sagaEventId = Optional.of(EventId.of("SomeSagaEventId"));
    var commandId = CommandId.of(randomUUID());
    var eventId = EventId.of(randomUUID());
    var eventMetaWithSaga = new EventMeta(eventId, 1, debitedAccount, commandId, 0, sagaEventId);

    var debited = new Debited(eventMetaWithSaga, creditedAcc, 300);

    // When
    var command = saga.apply(debited);

    // Then
    Assertions.assertThat(command).isPresent();
    Assertions.assertThat(command.get()).isInstanceOf(Credit.class);
    // And
    var credit = (Credit) command.get();
    Assertions.assertThat(credit.accountId()).isEqualTo(creditedAcc);
    Assertions.assertThat(credit.debitedAcc()).isEqualTo(debitedAccount);
  }
}
