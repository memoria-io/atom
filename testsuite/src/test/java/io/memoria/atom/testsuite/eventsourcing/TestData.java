package io.memoria.atom.testsuite.eventsourcing;

import io.memoria.atom.core.id.Id;
import io.memoria.atom.eventsourcing.aggregate.Decider;
import io.memoria.atom.eventsourcing.aggregate.Evolver;
import io.memoria.atom.eventsourcing.saga.Saga;
import io.memoria.atom.eventsourcing.state.StateId;

import java.util.concurrent.atomic.AtomicLong;
import java.util.function.Supplier;

public class TestData {
  // Rule
  public static final AtomicLong counter = new AtomicLong();
  public static final Supplier<Id> idSupplier = () -> Id.of(counter.getAndIncrement());
  public static final Supplier<Long> timeSupplier = () -> 0L;
  public static final Decider decider = new AccountDecider(idSupplier, timeSupplier);
  public static final Evolver evolver = new AccountEvolver();
  public static final Saga saga = new AccountSaga(idSupplier, timeSupplier);

  // Data
  public static final String alice = "alice";
  public static StateId aliceId = StateId.of(alice);

  public static final String bob = "bob";
  public static StateId bobId = StateId.of(bob);

  private TestData() {}
}
