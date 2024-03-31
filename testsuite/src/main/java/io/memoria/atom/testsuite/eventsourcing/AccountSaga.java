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
