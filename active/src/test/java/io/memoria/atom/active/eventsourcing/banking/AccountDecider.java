package io.memoria.atom.active.eventsourcing.banking;

import io.memoria.atom.active.eventsourcing.banking.command.*;
import io.memoria.atom.active.eventsourcing.banking.event.*;
import io.memoria.atom.active.eventsourcing.banking.state.ActiveAccount;
import io.memoria.atom.active.eventsourcing.banking.state.ClosedAccount;
import io.memoria.atom.active.eventsourcing.banking.state.User;
import io.memoria.atom.core.eventsourcing.exception.ESException;
import io.memoria.atom.core.eventsourcing.rule.Decider;
import io.vavr.control.Try;

public record AccountDecider() implements Decider<User, UserCommand, UserEvent> {
  @Override
  @SuppressWarnings("SwitchStatementWithTooFewBranches")
  public Try<UserEvent> apply(UserCommand command) {
    return switch (command) {
      case CreateAccount cmd -> Try.success(AccountCreated.by(cmd));
      default -> Try.failure(ESException.InvalidCommand.create(command));
    };
  }

  @Override
  public Try<UserEvent> apply(User state, UserCommand command) {
    return switch (state) {
      case ActiveAccount acc -> handle(acc, command);
      case ClosedAccount acc -> handle(acc, command);
    };
  }

  private Try<UserEvent> handle(ActiveAccount state, UserCommand command) {
    return switch (command) {
      case CreateAccount cmd -> Try.failure(ESException.InvalidCommand.create(state, cmd));
      case CreateTransfer cmd -> Try.success(TransferCreated.by(cmd));
      case HandleInboundTransfer cmd -> Try.success(InboundTransferAccepted.by(cmd));
      case MarkAsSuccessful cmd -> Try.success(OutboundTransferAccepted.by(cmd));
      case MarkAsRejected cmd -> Try.success(OutboundTransferRejected.by(cmd));
      case CloseAccount cmd -> Try.success(AccountClosed.by(cmd));
    };
  }

  @SuppressWarnings("SwitchStatementWithTooFewBranches")
  private Try<UserEvent> handle(ClosedAccount state, UserCommand command) {
    return switch (command) {
      case HandleInboundTransfer cmd -> Try.success(InboundTransferRejected.by(cmd));
      default -> Try.failure(ESException.InvalidCommand.create(command));
    };
  }
}
