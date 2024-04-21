package eventsourcing.domain;

import io.memoria.atom.core.id.Id;
import io.memoria.atom.eventsourcing.command.Command;
import io.memoria.atom.eventsourcing.event.Event;
import io.memoria.atom.eventsourcing.saga.Saga;
import eventsourcing.domain.command.ConfirmDebit;
import eventsourcing.domain.command.Credit;
import eventsourcing.domain.event.AccountEvent;
import eventsourcing.domain.event.CreditRejected;
import eventsourcing.domain.event.Credited;
import eventsourcing.domain.event.Debited;

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
