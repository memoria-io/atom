package io.memoria.atom.testsuite.eventsourcing.banking;

import io.memoria.atom.core.id.Id;
import io.memoria.atom.eventsourcing.CommandId;
import io.memoria.atom.eventsourcing.CommandMeta;
import io.memoria.atom.eventsourcing.EventMeta;
import io.memoria.atom.eventsourcing.StateId;
import io.memoria.atom.testsuite.eventsourcing.banking.state.OpenAccount;

import java.util.concurrent.atomic.AtomicLong;
import java.util.function.Supplier;

public class TestUtils {
  // Rule
  public static final AtomicLong counter = new AtomicLong();
  public static final Supplier<Id> idSupplier = () -> Id.of(counter.getAndIncrement());
  public static final Supplier<Long> timeSupplier = () -> 0L;
  public static final AccountDecider decider = new AccountDecider(idSupplier, timeSupplier);
  public static final AccountEvolver evolver = new AccountEvolver(idSupplier, timeSupplier);
  public static final AccountSaga saga = new AccountSaga(idSupplier, timeSupplier);

  // Data
  public static final String alice = "alice";
  public static StateId aliceId = StateId.of(alice);
  public static final CommandMeta aliceCommandMeta = commandMeta(aliceId);
  public static final EventMeta aliceEventMeta = eventMeta(aliceCommandMeta.commandId(), aliceId);

  public static final String bob = "bob";
  public static StateId bobId = StateId.of(bob);
  public static final CommandMeta bobCommandMeta = commandMeta(bobId);
  public static final EventMeta bobEventMeta = eventMeta(bobCommandMeta.commandId(), bobId);

  public static CommandMeta commandMeta(StateId stateId) {
    return new CommandMeta(stateId);
  }

  public static EventMeta eventMeta(CommandId commandId, StateId stateId) {
    return new EventMeta(commandId, stateId);
  }

  private TestUtils() {}

  public static OpenAccount createOpenAccount(int balance) {
    return new OpenAccount(aliceCommandMeta.stateId(), alice, balance);
  }
}
