package io.memoria.atom.testsuite.eventsourcing.banking;

import io.memoria.atom.core.id.Id;
import io.memoria.atom.eventsourcing.ESException;
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
import io.memoria.atom.testsuite.eventsourcing.banking.event.DebitRejected;
import io.memoria.atom.testsuite.eventsourcing.banking.event.Debited;
import io.memoria.atom.testsuite.eventsourcing.banking.event.NameChanged;
import io.memoria.atom.testsuite.eventsourcing.banking.state.Account;
import io.memoria.atom.testsuite.eventsourcing.banking.state.ClosedAccount;
import io.memoria.atom.testsuite.eventsourcing.banking.state.OpenAccount;
import io.vavr.control.Try;

import java.util.function.Supplier;

import static io.vavr.control.Try.failure;
import static io.vavr.control.Try.success;

public record AccountDecider(Supplier<Id> idSupplier, Supplier<Long> timeSupplier)
        implements Decider<Account, AccountCommand, AccountEvent> {

  @Override
  @SuppressWarnings("SwitchStatementWithTooFewBranches")
  public Try<AccountEvent> apply(AccountCommand command) {
    return eventMeta(command).flatMap(meta -> switch (command) {
      case CreateAccount cmd -> success(new AccountCreated(meta, cmd.accountName(), cmd.balance()));
      default -> failure(ESException.InvalidCommand.of(command));
    });
  }

  @Override
  public Try<AccountEvent> apply(Account state, AccountCommand command) {
    return eventMeta(state, command).flatMap(meta -> switch (state) {
      case OpenAccount openAccount -> handle(openAccount, command, meta);
      case ClosedAccount acc -> handle(acc, command, meta);
    });
  }

  private Try<AccountEvent> handle(OpenAccount account, AccountCommand command, EventMeta meta) {
    return switch (command) {
      case CreateAccount cmd -> failure(ESException.InvalidCommand.of(account, cmd));
      case ChangeName cmd -> success(new NameChanged(meta, cmd.name()));
      case Debit cmd -> tryToDebit(account, meta, cmd);
      case Credit cmd -> success(new Credited(meta, cmd.debitedAcc(), cmd.amount()));
      case ConfirmDebit cmd -> success(new DebitConfirmed(meta));
      case CloseAccount cmd -> tryToClose(account, meta);
    };
  }

  private Try<AccountEvent> handle(ClosedAccount state, AccountCommand command, EventMeta meta) {
    return switch (command) {
      case Credit cmd -> success(new CreditRejected(meta, cmd.debitedAcc(), cmd.amount()));
      case ConfirmDebit cmd -> success(new DebitConfirmed(meta));
      case ChangeName cmd -> failure(ESException.InvalidCommand.of(state, cmd));
      case Debit cmd -> failure(ESException.InvalidCommand.of(state, cmd));
      case CreateAccount cmd -> failure(ESException.InvalidCommand.of(state, cmd));
      case CloseAccount cmd -> failure(ESException.InvalidCommand.of(state, cmd));
    };
  }

  private static Try<AccountEvent> tryToDebit(OpenAccount account, EventMeta meta, Debit cmd) {
    if (account.canDebit(cmd.amount())) {
      return success(new Debited(meta, cmd.creditedAcc(), cmd.amount()));
    } else {
      return success(new DebitRejected(meta));
    }
  }

  private static Try<AccountEvent> tryToClose(OpenAccount openAccount, EventMeta meta) {
    if (openAccount.hasOngoingDebit()) {
      return success(new ClosureRejected(meta));
    } else {
      return success(new AccountClosed(meta));
    }
  }
}
