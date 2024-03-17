package io.memoria.atom.testsuite.eventsourcing;

import io.memoria.atom.core.id.Id;
import io.memoria.atom.eventsourcing.command.Command;
import io.memoria.atom.eventsourcing.event.Event;
import io.memoria.atom.eventsourcing.rule.Saga;
import io.memoria.atom.testsuite.eventsourcing.command.ConfirmDebit;
import io.memoria.atom.testsuite.eventsourcing.command.Credit;
import io.memoria.atom.testsuite.eventsourcing.event.AccountEvent;
import io.memoria.atom.testsuite.eventsourcing.event.CreditRejected;
import io.memoria.atom.testsuite.eventsourcing.event.Credited;
import io.memoria.atom.testsuite.eventsourcing.event.Debited;

import java.util.Optional;
import java.util.function.Supplier;

public record AccountSaga(Supplier<Id> idSupplier, Supplier<Long> timeSupplier) implements Saga {

  @Override
  public Optional<Command> apply(Event event) {
    if (event instanceof AccountEvent accountEvent) {
      return apply(accountEvent);
    } else {
      return Optional.empty();
    }
  }

  public Optional<Command> apply(AccountEvent event) {
    return switch (event) {
      case Debited e ->
              Optional.of(new Credit(commandMeta(e.creditedAcc(), e.meta().eventId()), e.accountId(), e.amount()));
      case Credited e -> Optional.of(new ConfirmDebit(commandMeta(e.debitedAcc(), e.meta().eventId()), e.accountId()));
      case CreditRejected e ->
              Optional.of(new Credit(commandMeta(e.debitedAcc(), e.meta().eventId()), e.accountId(), e.amount()));
      default -> Optional.empty();
    };
  }
}
