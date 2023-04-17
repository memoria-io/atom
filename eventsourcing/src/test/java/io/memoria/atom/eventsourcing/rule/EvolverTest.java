package io.memoria.atom.eventsourcing.rule;

import io.memoria.atom.core.id.Id;
import io.memoria.atom.eventsourcing.usecase.banking.AccountEvolver;
import io.memoria.atom.eventsourcing.usecase.banking.event.*;
import io.memoria.atom.eventsourcing.usecase.banking.state.OpenAccount;
import org.junit.jupiter.api.Test;
import reactor.core.publisher.Flux;
import reactor.test.StepVerifier;

class EvolverTest {
  private final Id id = Id.of(0);
  private final AccountCreated accCreated = new AccountCreated(id, id, Id.of("bob"), "bob0", 0);
  private final NameChanged nameChanged1 = new NameChanged(id, id, Id.of("bob"), "bob1");
  private final NameChanged nameChanged2 = new NameChanged(id, id, Id.of("bob"), "bob2");
  private final Credited credited = new Credited(id, id, Id.of("bob"), Id.of("jan"), 10);

  @Test
  void reduce() {
    // When
    var events = Flux.<AccountEvent>just(accCreated, nameChanged1, nameChanged2, credited);
    var evolver = new AccountEvolver();

    // Then
    StepVerifier.create(evolver.reduce(events))
                .expectNext(new OpenAccount(Id.of("bob"), "bob2", 10, 0))
                .verifyComplete();
  }

  @Test
  void accumulate() {
    // When
    var events = Flux.<AccountEvent>just(accCreated, nameChanged1, nameChanged2, credited);
    var evolver = new AccountEvolver();

    // Then
    StepVerifier.create(evolver.accumulate(events))
                .expectNext(new OpenAccount(Id.of("bob"), "bob0", 0, 0))
                .expectNext(new OpenAccount(Id.of("bob"), "bob1", 0, 0))
                .expectNext(new OpenAccount(Id.of("bob"), "bob2", 0, 0))
                .expectNext(new OpenAccount(Id.of("bob"), "bob2", 10, 0))
                .verifyComplete();
  }
}
