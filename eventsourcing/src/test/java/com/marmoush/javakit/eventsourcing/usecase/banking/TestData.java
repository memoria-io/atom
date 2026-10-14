package com.marmoush.javakit.eventsourcing.usecase.banking;

import com.marmoush.javakit.core.id.Id;
import com.marmoush.javakit.core.id.Ids;
import com.marmoush.javakit.eventsourcing.aggregate.Decider;
import com.marmoush.javakit.eventsourcing.aggregate.Evolver;
import com.marmoush.javakit.eventsourcing.saga.Saga;
import com.marmoush.javakit.eventsourcing.state.StateId;
import com.marmoush.javakit.eventsourcing.state.StateIds;

import java.util.concurrent.atomic.AtomicLong;
import java.util.function.Supplier;

public class TestData {
  // Rule
  public static final AtomicLong counter = new AtomicLong();
  public static final Supplier<Id> idSupplier = () -> Ids.of(counter.getAndIncrement());
  public static final Supplier<Long> timeSupplier = () -> 0L;
  public static final Decider decider = new AccountDecider(idSupplier, timeSupplier);
  public static final Evolver evolver = new AccountEvolver();
  public static final Saga saga = new AccountSaga(idSupplier, timeSupplier);

  // Data
  public static final String alice = "alice";
  public static StateId aliceId = StateIds.of(alice);

  public static final String bob = "bob";
  public static StateId bobId = StateIds.of(bob);

  private TestData() {}
}
