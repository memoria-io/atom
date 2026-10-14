package com.marmoush.javakit.eventsourcing.usecase.banking;

import com.marmoush.javakit.core.id.Id;
import com.marmoush.javakit.eventsourcing.command.Command;
import com.marmoush.javakit.eventsourcing.event.Event;
import com.marmoush.javakit.eventsourcing.saga.Saga;
import com.marmoush.javakit.eventsourcing.usecase.banking.command.ConfirmDebit;
import com.marmoush.javakit.eventsourcing.usecase.banking.command.Credit;
import com.marmoush.javakit.eventsourcing.usecase.banking.event.AccountEvent;
import com.marmoush.javakit.eventsourcing.usecase.banking.event.CreditRejected;
import com.marmoush.javakit.eventsourcing.usecase.banking.event.Credited;
import com.marmoush.javakit.eventsourcing.usecase.banking.event.Debited;

import java.util.Optional;
import java.util.function.Supplier;

public record AccountSaga(Supplier<Id> idSupplier, Supplier<Long> timeSupplier) implements Saga {

  @Override
  public Optional<Command> react(Event event) {
    if (event instanceof AccountEvent accountEvent) {
      return apply(accountEvent);
    } else {
      return Optional.empty();
    }
  }

  public Optional<Command> apply(AccountEvent event) {
    return switch (event) {
      case Debited debited -> Optional.of(new Credit(commandMeta(debited.creditedAcc(), debited),
                                                     debited.accountId(),
                                                     debited.amount()));
      case Credited credited ->
              Optional.of(new ConfirmDebit(commandMeta(credited.debitedAcc(), credited), credited.accountId()));
      case CreditRejected rejected -> Optional.of(new Credit(commandMeta(rejected.debitedAcc(), rejected),
                                                             rejected.accountId(),
                                                             rejected.amount()));
      default -> Optional.empty();
    };
  }
}
