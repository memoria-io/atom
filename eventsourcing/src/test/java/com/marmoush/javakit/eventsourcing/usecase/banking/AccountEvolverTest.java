package com.marmoush.javakit.eventsourcing.usecase.banking;

import com.marmoush.javakit.eventsourcing.command.CommandIds;
import com.marmoush.javakit.eventsourcing.event.EventIds;
import com.marmoush.javakit.eventsourcing.event.EventMeta;
import com.marmoush.javakit.eventsourcing.state.StateMeta;
import com.marmoush.javakit.eventsourcing.usecase.banking.event.Debited;
import com.marmoush.javakit.eventsourcing.usecase.banking.state.OpenAccount;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;

import static com.marmoush.javakit.eventsourcing.usecase.banking.TestData.alice;
import static com.marmoush.javakit.eventsourcing.usecase.banking.TestData.aliceId;
import static com.marmoush.javakit.eventsourcing.usecase.banking.TestData.bobId;
import static com.marmoush.javakit.eventsourcing.usecase.banking.TestData.evolver;
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
