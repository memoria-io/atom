package io.memoria.atom.eventsourcing.usecase.banking;

import io.memoria.atom.eventsourcing.rule.Saga;
import io.memoria.atom.eventsourcing.usecase.banking.command.AccountCommand;
import io.memoria.atom.eventsourcing.usecase.banking.command.ConfirmDebit;
import io.memoria.atom.eventsourcing.usecase.banking.command.Credit;
import io.memoria.atom.core.eventsourcing.usecase.banking.event.*;
import io.memoria.atom.eventsourcing.usecase.banking.event.*;
import io.vavr.control.Option;

public record AccountSaga() implements Saga<AccountEvent, AccountCommand> {

  @Override
  public Option<AccountCommand> apply(AccountEvent accountEvent) {
    return switch (accountEvent) {
      case Debited e -> Option.some(Credit.of(e.creditedAcc(), e.debitedAcc(), e.amount()));
      case Credited e -> Option.some(ConfirmDebit.of(e.debitedAcc()));
      case CreditRejected e -> Option.some(Credit.of(e.debitedAcc(), e.creditedAcc(), e.amount()));
      default -> Option.none();
    };
  }
}
