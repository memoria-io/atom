package io.memoria.atom.reactive.banking;

import io.memoria.atom.reactive.Utils;
import io.memoria.atom.reactive.banking.event.AccountClosed;
import io.memoria.atom.reactive.banking.event.AccountCreated;
import io.memoria.atom.reactive.banking.event.InboundTransferAccepted;
import io.memoria.atom.reactive.banking.event.InboundTransferRejected;
import io.memoria.atom.reactive.banking.event.OutboundTransferAccepted;
import io.memoria.atom.reactive.banking.event.OutboundTransferRejected;
import io.memoria.atom.reactive.banking.event.TransferCreated;
import io.memoria.atom.reactive.banking.event.UserEvent;
import io.memoria.atom.reactive.banking.state.User;
import io.memoria.atom.reactive.banking.state.Visitor;
import io.memoria.atom.reactive.banking.command.AccountCommand;
import io.memoria.atom.reactive.banking.command.CloseAccount;
import io.memoria.atom.reactive.banking.command.CreateAccount;
import io.memoria.atom.reactive.banking.command.CreateTransfer;
import io.memoria.atom.reactive.banking.command.HandleInboundTransfer;
import io.memoria.atom.reactive.banking.command.MarkAsRejected;
import io.memoria.atom.reactive.banking.command.MarkAsSuccessful;
import io.memoria.atom.reactive.banking.state.ActiveAccount;
import io.memoria.atom.reactive.banking.state.ClosedAccount;
import io.memoria.atom.core.eventsourcing.rule.Decider;
import io.vavr.control.Try;

public record AccountDecider() implements Decider<User, AccountCommand, UserEvent> {

  @Override
  public Try<UserEvent> apply(User user, AccountCommand accountCommand) {
    return switch (user) {
      case Visitor acc -> handle(acc, accountCommand);
      case ActiveAccount acc -> handle(acc, accountCommand);
      case ClosedAccount acc -> handle(acc, accountCommand);
    };
  }

  @SuppressWarnings("SwitchStatementWithTooFewBranches")
  private Try<UserEvent> handle(Visitor visitor, AccountCommand accountCommand) {
    return switch (accountCommand) {
      case CreateAccount cmd -> Try.success(AccountCreated.by(cmd));
      default -> Utils.invalidOperation(visitor, accountCommand);
    };
  }

  private Try<UserEvent> handle(ActiveAccount activeAccount, AccountCommand accountCommand) {
    return switch (accountCommand) {
      case CreateAccount cmd -> Utils.invalidOperation(activeAccount, cmd);
      case CreateTransfer cmd -> Try.success(TransferCreated.by(cmd));
      case HandleInboundTransfer cmd -> Try.success(InboundTransferAccepted.by(cmd));
      case MarkAsSuccessful cmd -> Try.success(OutboundTransferAccepted.by(cmd));
      case MarkAsRejected cmd -> Try.success(OutboundTransferRejected.by(cmd));
      case CloseAccount cmd -> Try.success(AccountClosed.by(cmd));
    };
  }

  @SuppressWarnings("SwitchStatementWithTooFewBranches")
  private Try<UserEvent> handle(ClosedAccount closedAccount, AccountCommand accountCommand) {
    return switch (accountCommand) {
      case HandleInboundTransfer cmd -> Try.success(InboundTransferRejected.by(cmd));
      default -> Utils.invalidOperation(closedAccount, accountCommand);
    };
  }
}