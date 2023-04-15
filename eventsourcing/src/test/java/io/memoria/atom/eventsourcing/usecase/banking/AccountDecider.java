package io.memoria.atom.eventsourcing.usecase.banking;

import io.memoria.atom.eventsourcing.exception.ESException;
import io.memoria.atom.eventsourcing.rule.Decider;
import io.memoria.atom.eventsourcing.usecase.banking.command.*;
import io.memoria.atom.eventsourcing.usecase.banking.event.*;
import io.memoria.atom.eventsourcing.usecase.banking.state.OpenAccount;
import io.memoria.atom.eventsourcing.usecase.banking.state.Account;
import io.memoria.atom.eventsourcing.usecase.banking.state.ClosedAccount;
import io.vavr.control.Try;

public record AccountDecider() implements Decider<Account, AccountCommand, AccountEvent> {

  @Override
  @SuppressWarnings("SwitchStatementWithTooFewBranches")
  public Try<AccountEvent> apply(AccountCommand accountCommand) {
    return switch (accountCommand) {
      case CreateAccount cmd -> Try.success(AccountCreated.from(cmd));
      default -> Try.failure(ESException.InvalidCommand.create(accountCommand));
    };
  }

  @Override
  public Try<AccountEvent> apply(Account state, AccountCommand command) {
    return switch (state) {
      case OpenAccount openAccount -> handle(openAccount, command);
      case ClosedAccount acc -> handle(acc, command);
    };
  }

  private Try<AccountEvent> handle(OpenAccount state, AccountCommand command) {
    return switch (command) {
      case CreateAccount cmd -> Try.failure(ESException.InvalidCommand.create(state, cmd));
      case ChangeName cmd -> Try.success(NameChanged.from(state, cmd));
      case Debit cmd -> Try.success(Debited.from(state, cmd));
      case Credit cmd -> Try.success(Credited.from(state, cmd));
      case ConfirmDebit cmd -> Try.success(DebitConfirmed.from(state, cmd));
      case CloseAccount cmd -> tryToClose(state, cmd);
    };
  }

  private Try<AccountEvent> handle(ClosedAccount state, AccountCommand command) {
    return switch (command) {
      case Credit cmd -> Try.success(CreditRejected.from(state, cmd));
      case ConfirmDebit cmd -> Try.success(DebitConfirmed.from(state, cmd));
      case ChangeName cmd -> Try.failure(ESException.InvalidCommand.create(state, cmd));
      case Debit cmd -> Try.failure(ESException.InvalidCommand.create(state, cmd));
      case CreateAccount cmd -> Try.failure(ESException.InvalidCommand.create(state, cmd));
      case CloseAccount cmd -> Try.failure(ESException.InvalidCommand.create(state, cmd));
    };
  }

  private Try<AccountEvent> tryToClose(OpenAccount openAccount, CloseAccount cmd) {
    if (openAccount.hasOngoingDebit())
      return Try.success(ClosureRejected.from(openAccount, cmd));
    return Try.success(AccountClosed.from(openAccount, cmd));
  }
}
