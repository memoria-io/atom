package io.memoria.atom.core.eventsourcing.usecase.banking;

import io.memoria.atom.core.eventsourcing.exception.ESException;
import io.memoria.atom.core.eventsourcing.rule.Decider;
import io.memoria.atom.core.eventsourcing.usecase.banking.command.*;
import io.memoria.atom.core.eventsourcing.usecase.banking.event.*;
import io.memoria.atom.core.eventsourcing.usecase.banking.state.Acc;
import io.memoria.atom.core.eventsourcing.usecase.banking.state.Account;
import io.memoria.atom.core.eventsourcing.usecase.banking.state.ClosedAccount;
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
      case Acc acc -> handle(acc, command);
      case ClosedAccount acc -> handle(acc, command);
    };
  }

  private Try<AccountEvent> handle(Acc state, AccountCommand command) {
    return switch (command) {
      case CreateAccount cmd -> Try.failure(ESException.InvalidCommand.create(state, cmd));
      case ChangeName cmd -> Try.success(NameChanged.from(cmd));
      case Debit cmd -> Try.success(Debited.from(cmd));
      case Credit cmd -> Try.success(Credited.from(cmd));
      case ConfirmDebit cmd -> Try.success(DebitConfirmed.from(cmd));
      case CloseAccount cmd -> tryToClose(state, cmd);
    };
  }

  private Try<AccountEvent> handle(ClosedAccount state, AccountCommand command) {
    return switch (command) {
      case Credit cmd -> Try.success(CreditRejected.from(cmd));
      case ConfirmDebit cmd -> Try.success(DebitConfirmed.from(cmd));
      case ChangeName cmd -> Try.failure(ESException.InvalidCommand.create(state, cmd));
      case Debit cmd -> Try.failure(ESException.InvalidCommand.create(state, cmd));
      case CreateAccount cmd -> Try.failure(ESException.InvalidCommand.create(state, cmd));
      case CloseAccount cmd -> Try.failure(ESException.InvalidCommand.create(state, cmd));
    };
  }

  private Try<AccountEvent> tryToClose(Acc acc, CloseAccount cmd) {
    if (acc.hasOngoingDebit())
      return Try.success(ClosureRejected.from(cmd));
    return Try.success(AccountClosed.from(cmd));
  }
}
