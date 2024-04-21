package eventsourcing;

import io.memoria.atom.eventsourcing.command.CommandId;
import io.memoria.atom.eventsourcing.event.EventId;
import io.memoria.atom.eventsourcing.event.EventMeta;
import io.memoria.atom.eventsourcing.state.StateMeta;
import eventsourcing.domain.event.Debited;
import eventsourcing.domain.state.OpenAccount;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;

import static eventsourcing.TestData.alice;
import static eventsourcing.TestData.aliceId;
import static eventsourcing.TestData.bobId;
import static eventsourcing.TestData.evolver;
import static java.util.UUID.randomUUID;

class AccountEvolverTest {
  @Test
  void evolve() {
    // Given
    var openAccount = new OpenAccount(new StateMeta(aliceId), alice, 500);
    var debited = new Debited(new EventMeta(EventId.of(randomUUID()), 1, aliceId, CommandId.of(randomUUID())),
                              bobId,
                              300);

    // When
    var acc = (OpenAccount) evolver.evolve(openAccount, debited);

    // Then
    Assertions.assertThat(acc.balance()).isEqualTo(200);
  }
}
