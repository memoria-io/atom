package io.memoria.atom.testsuite.eventsourcing.banking;

import io.memoria.atom.core.id.Id;
import io.memoria.atom.eventsourcing.ESException;
import io.memoria.atom.eventsourcing.EventId;
import io.memoria.atom.eventsourcing.EventMeta;
import io.memoria.atom.eventsourcing.rule.Decider;
import io.memoria.atom.testsuite.eventsourcing.banking.command.AccountCommand;
import io.memoria.atom.testsuite.eventsourcing.banking.command.ChangeName;
import io.memoria.atom.testsuite.eventsourcing.banking.command.CloseAccount;
import io.memoria.atom.testsuite.eventsourcing.banking.command.ConfirmDebit;
import io.memoria.atom.testsuite.eventsourcing.banking.command.CreateAccount;
import io.memoria.atom.testsuite.eventsourcing.banking.command.Credit;
import io.memoria.atom.testsuite.eventsourcing.banking.command.Debit;
import io.memoria.atom.testsuite.eventsourcing.banking.event.AccountClosed;
import io.memoria.atom.testsuite.eventsourcing.banking.event.AccountCreated;
import io.memoria.atom.testsuite.eventsourcing.banking.event.AccountEvent;
import io.memoria.atom.testsuite.eventsourcing.banking.event.ClosureRejected;
import io.memoria.atom.testsuite.eventsourcing.banking.event.CreditRejected;
import io.memoria.atom.testsuite.eventsourcing.banking.event.Credited;
import io.memoria.atom.testsuite.eventsourcing.banking.event.DebitConfirmed;
import io.memoria.atom.testsuite.eventsourcing.banking.event.Debited;
import io.memoria.atom.testsuite.eventsourcing.banking.event.NameChanged;
import io.memoria.atom.testsuite.eventsourcing.banking.state.Account;
import io.memoria.atom.testsuite.eventsourcing.banking.state.ClosedAccount;
import io.memoria.atom.testsuite.eventsourcing.banking.state.OpenAccount;
import io.vavr.control.Try;

import java.util.function.Supplier;

public record AccountDecider(Supplier<Id> idSupplier, Supplier<Long> timeSupplier)
        implements Decider<Account, AccountCommand, AccountEvent> {

  @Override
  @SuppressWarnings("SwitchStatementWithTooFewBranches")
  public Try<AccountEvent> apply(AccountCommand command) {
    var meta = getMeta(command);
    return switch (command) {
      case CreateAccount cmd -> Try.success(new AccountCreated(meta, cmd.accountName(), cmd.balance()));
      default -> Try.failure(ESException.InvalidCommand.of(command));
    };
  }

  private EventMeta getMeta(AccountCommand command) {
    return new EventMeta(EventId.of(idSupplier.get()),
                         command.meta().commandId(),
                         command.accountId(),
                         timeSupplier.get());
  }

  @Override
  public Try<AccountEvent> apply(Account state, AccountCommand command) {
    return switch (state) {
      case OpenAccount openAccount -> handle(openAccount, command);
      case ClosedAccount acc -> handle(acc, command);
    };
  }

  private Try<AccountEvent> handle(OpenAccount state, AccountCommand command) {
    var meta = getMeta(command);
    return switch (command) {
      case CreateAccount cmd -> Try.failure(ESException.InvalidCommand.of(state, cmd));
      case ChangeName cmd -> Try.success(new NameChanged(meta, cmd.name()));
      case Debit cmd -> Try.success(new Debited(meta, cmd.creditedAcc(), cmd.amount()));
      case Credit cmd -> Try.success(new Credited(meta, cmd.debitedAcc(), cmd.amount()));
      case ConfirmDebit cmd -> Try.success(new DebitConfirmed(meta));
      case CloseAccount cmd -> tryToClose(state, cmd);
    };
  }

  private Try<AccountEvent> handle(ClosedAccount state, AccountCommand command) {
    var meta = getMeta(command);
    return switch (command) {
      case Credit cmd -> Try.success(new CreditRejected(meta, cmd.debitedAcc(), cmd.amount()));
      case ConfirmDebit cmd -> Try.success(new DebitConfirmed(meta)); // TODO validate creditor
      case ChangeName cmd -> Try.failure(ESException.InvalidCommand.of(state, cmd));
      case Debit cmd -> Try.failure(ESException.InvalidCommand.of(state, cmd));
      case CreateAccount cmd -> Try.failure(ESException.InvalidCommand.of(state, cmd));
      case CloseAccount cmd -> Try.failure(ESException.InvalidCommand.of(state, cmd));
    };
  }

  private Try<AccountEvent> tryToClose(OpenAccount openAccount, CloseAccount command) {
    var meta = new EventMeta(EventId.of(idSupplier.get()),
                             command.meta().commandId(),
                             command.accountId(),
                             timeSupplier.get());
    if (openAccount.hasOngoingDebit())
      return Try.success(new ClosureRejected(meta));
    return Try.success(new AccountClosed(meta));
  }
}
