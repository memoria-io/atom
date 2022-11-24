package io.memoria.atom.active.eventsourcing.banking;

import io.memoria.atom.active.eventsourcing.Utils;
import io.memoria.atom.active.eventsourcing.banking.command.CloseAccount;
import io.memoria.atom.active.eventsourcing.banking.command.CreateAccount;
import io.memoria.atom.active.eventsourcing.banking.command.CreateTransfer;
import io.memoria.atom.active.eventsourcing.banking.command.HandleInboundTransfer;
import io.memoria.atom.active.eventsourcing.banking.command.MarkAsRejected;
import io.memoria.atom.active.eventsourcing.banking.command.MarkAsSuccessful;
import io.memoria.atom.active.eventsourcing.banking.command.UserCommand;
import io.memoria.atom.active.eventsourcing.banking.event.AccountClosed;
import io.memoria.atom.active.eventsourcing.banking.event.AccountCreated;
import io.memoria.atom.active.eventsourcing.banking.event.InboundTransferAccepted;
import io.memoria.atom.active.eventsourcing.banking.event.InboundTransferRejected;
import io.memoria.atom.active.eventsourcing.banking.event.OutboundTransferAccepted;
import io.memoria.atom.active.eventsourcing.banking.event.OutboundTransferRejected;
import io.memoria.atom.active.eventsourcing.banking.event.TransferCreated;
import io.memoria.atom.active.eventsourcing.banking.event.UserEvent;
import io.memoria.atom.active.eventsourcing.banking.state.ActiveAccount;
import io.memoria.atom.active.eventsourcing.banking.state.ClosedAccount;
import io.memoria.atom.active.eventsourcing.banking.state.User;
import io.memoria.atom.active.eventsourcing.banking.state.Visitor;
import io.memoria.atom.core.eventsourcing.rule.Decider;
import io.vavr.control.Try;

public record AccountDecider() implements Decider<User, UserCommand, UserEvent> {

  @Override
  public Try<UserEvent> apply(User user, UserCommand userCommand) {
    return switch (user) {
      case Visitor acc -> handle(acc, userCommand);
      case ActiveAccount acc -> handle(acc, userCommand);
      case ClosedAccount acc -> handle(acc, userCommand);
    };
  }

  @SuppressWarnings("SwitchStatementWithTooFewBranches")
  private Try<UserEvent> handle(Visitor visitor, UserCommand userCommand) {
    return switch (userCommand) {
      case CreateAccount cmd -> Try.success(AccountCreated.by(cmd));
      default -> Utils.invalidOperation(visitor, userCommand);
    };
  }

  private Try<UserEvent> handle(ActiveAccount activeAccount, UserCommand userCommand) {
    return switch (userCommand) {
      case CreateAccount cmd -> Utils.invalidOperation(activeAccount, cmd);
      case CreateTransfer cmd -> Try.success(TransferCreated.by(cmd));
      case HandleInboundTransfer cmd -> Try.success(InboundTransferAccepted.by(cmd));
      case MarkAsSuccessful cmd -> Try.success(OutboundTransferAccepted.by(cmd));
      case MarkAsRejected cmd -> Try.success(OutboundTransferRejected.by(cmd));
      case CloseAccount cmd -> Try.success(AccountClosed.by(cmd));
    };
  }

  @SuppressWarnings("SwitchStatementWithTooFewBranches")
  private Try<UserEvent> handle(ClosedAccount closedAccount, UserCommand userCommand) {
    return switch (userCommand) {
      case HandleInboundTransfer cmd -> Try.success(InboundTransferRejected.by(cmd));
      default -> Utils.invalidOperation(closedAccount, userCommand);
    };
  }
}
