package io.memoria.atom.testsuite.eventsourcing.banking;

import io.memoria.atom.core.id.Id;
import io.memoria.atom.eventsourcing.CommandMeta;
import io.memoria.atom.eventsourcing.StateId;
import io.memoria.atom.testsuite.eventsourcing.banking.command.CreateAccount;
import org.junit.jupiter.api.Test;

import java.util.concurrent.atomic.AtomicLong;
import java.util.function.Supplier;

class AccountDeciderTest {
  private static final AtomicLong counter = new AtomicLong();
  private final Supplier<Id> idSupplier = () -> Id.of(counter.getAndIncrement());
  private final Supplier<Long> timeSupplier = () -> 0L;
  private final AccountDecider decider = new AccountDecider(idSupplier, timeSupplier);

  @Test
  void decide() {
    var alice = StateId.of("alice");
    var aliceMeta = new CommandMeta(alice);
    var bob = StateId.of("bob");
    var bobMeta = new CommandMeta(bob);
    decider.apply(new CreateAccount(aliceMeta,alice.,))
  }
}
