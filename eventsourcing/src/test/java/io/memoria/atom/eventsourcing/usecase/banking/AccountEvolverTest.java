package io.memoria.atom.eventsourcing.usecase.banking;

import io.memoria.atom.eventsourcing.command.CommandIds;
import io.memoria.atom.eventsourcing.event.EventIds;
import io.memoria.atom.eventsourcing.event.EventMeta;
import io.memoria.atom.eventsourcing.state.StateMeta;
import io.memoria.atom.eventsourcing.usecase.banking.event.Debited;
import io.memoria.atom.eventsourcing.usecase.banking.state.OpenAccount;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;

import static io.memoria.atom.eventsourcing.usecase.banking.TestData.alice;
import static io.memoria.atom.eventsourcing.usecase.banking.TestData.aliceId;
import static io.memoria.atom.eventsourcing.usecase.banking.TestData.bobId;
import static io.memoria.atom.eventsourcing.usecase.banking.TestData.evolver;
import static java.util.UUID.randomUUID;

class AccountEvolverTest {
  @Test
  void evolve() {
    // Given
    var openAccount = new OpenAccount(new StateMeta(aliceId), alice, 500);
    var debited = new Debited(new EventMeta(EventIds.of(randomUUID()), 1, aliceId, CommandIds.of(randomUUID())),
                              bobId,
                              300);

    // When
    var acc = (OpenAccount) evolver.evolve(openAccount, debited);

    // Then
    Assertions.assertThat(acc.balance()).isEqualTo(200);
  }
}
