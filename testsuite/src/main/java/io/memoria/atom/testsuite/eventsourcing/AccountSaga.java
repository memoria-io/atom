package io.memoria.atom.testsuite.eventsourcing;

import io.memoria.atom.core.id.Id;
import io.memoria.atom.eventsourcing.rule.Saga;
import io.memoria.atom.testsuite.eventsourcing.command.AccountCommand;
import io.memoria.atom.testsuite.eventsourcing.command.ConfirmDebit;
import io.memoria.atom.testsuite.eventsourcing.command.Credit;
import io.memoria.atom.testsuite.eventsourcing.event.AccountEvent;
import io.memoria.atom.testsuite.eventsourcing.event.CreditRejected;
import io.memoria.atom.testsuite.eventsourcing.event.Credited;
import io.memoria.atom.testsuite.eventsourcing.event.Debited;
import io.vavr.control.Option;

import java.util.function.Supplier;

public record AccountSaga(Supplier<Id> idSupplier, Supplier<Long> timeSupplier)
        implements Saga<AccountEvent, AccountCommand> {

  @Override
  public Option<AccountCommand> apply(AccountEvent event) {
    return switch (event) {
      case Debited e ->
              Option.some(new Credit(commandMeta(e.creditedAcc(), e.meta().eventId()), e.accountId(), e.amount()));
      case Credited e -> Option.some(new ConfirmDebit(commandMeta(e.debitedAcc(), e.meta().eventId()), e.accountId()));
      case CreditRejected e ->
              Option.some(new Credit(commandMeta(e.debitedAcc(), e.meta().eventId()), e.accountId(), e.amount()));
      default -> Option.none();
    };
  }
}
