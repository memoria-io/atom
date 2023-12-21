package io.memoria.atom.testsuite.eventsourcing;

import io.memoria.atom.eventsourcing.CommandId;
import io.memoria.atom.eventsourcing.EventMeta;
import io.memoria.atom.testsuite.eventsourcing.command.Credit;
import io.memoria.atom.testsuite.eventsourcing.event.Debited;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;

import static io.memoria.atom.testsuite.eventsourcing.TestData.aliceId;
import static io.memoria.atom.testsuite.eventsourcing.TestData.bobId;
import static io.memoria.atom.testsuite.eventsourcing.TestData.saga;

class AccountSagaTest {
  @Test
  void evolve() {
    // Given
    var debited = new Debited(new EventMeta(CommandId.of(), 1, aliceId), bobId, 300);

    // When
    var credit = (Credit) saga.apply(debited).get();

    // Then
    Assertions.assertThat(credit.accountId()).isEqualTo(bobId);
    Assertions.assertThat(credit.debitedAcc()).isEqualTo(aliceId);
  }
}
