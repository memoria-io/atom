package io.memoria.atom.eventsourcing.rule;

import io.memoria.atom.core.id.Id;
import io.memoria.atom.eventsourcing.usecase.banking.AccountEvolver;
import io.memoria.atom.eventsourcing.usecase.banking.event.*;
import io.memoria.atom.eventsourcing.usecase.banking.state.OpenAccount;
import org.junit.jupiter.api.Test;
import reactor.core.publisher.Flux;
import reactor.test.StepVerifier;

class EvolverTest {
  @Test
  void reduce() {
    // Given
    var accCreated = new AccountCreated(Id.of(0), Id.of(0), Id.of("bob"), "bob0", 0);
    var nameChanged1 = new NameChanged(Id.of(0), Id.of(0), Id.of("bob"), "bob1");
    var nameChanged2 = new NameChanged(Id.of(0), Id.of(0), Id.of("bob"), "bob2");
    var credited = new Credited(Id.of(0), Id.of(0), Id.of("bob"), Id.of("jan"), 10);

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
    // Given
    var accCreated = new AccountCreated(Id.of(0), Id.of(0), Id.of("bob"), "bob0", 0);
    var nameChanged1 = new NameChanged(Id.of(0), Id.of(0), Id.of("bob"), "bob1");
    var nameChanged2 = new NameChanged(Id.of(0), Id.of(0), Id.of("bob"), "bob2");
    var credited = new Credited(Id.of(0), Id.of(0), Id.of("bob"), Id.of("jan"), 10);

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
