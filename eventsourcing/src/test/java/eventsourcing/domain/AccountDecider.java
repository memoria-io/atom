package eventsourcing.domain;

import eventsourcing.domain.event.DebitRejected;
import io.memoria.atom.core.id.Id;
import io.memoria.atom.eventsourcing.aggregate.Decider;
import io.memoria.atom.eventsourcing.command.Command;
import io.memoria.atom.eventsourcing.command.exceptions.CommandException;
import io.memoria.atom.eventsourcing.command.exceptions.InvalidCommand;
import io.memoria.atom.eventsourcing.command.exceptions.MismatchingCommandState;
import io.memoria.atom.eventsourcing.command.exceptions.UnknownCommandRTE;
import io.memoria.atom.eventsourcing.event.Event;
import io.memoria.atom.eventsourcing.event.EventMeta;
import io.memoria.atom.eventsourcing.state.State;
import io.memoria.atom.eventsourcing.state.exceptions.UnknownState;
import eventsourcing.domain.command.AccountCommand;
import eventsourcing.domain.command.ChangeName;
import eventsourcing.domain.command.CloseAccount;
import eventsourcing.domain.command.ConfirmDebit;
import eventsourcing.domain.command.CreateAccount;
import eventsourcing.domain.command.Credit;
import eventsourcing.domain.command.Debit;
import eventsourcing.domain.event.AccountClosed;
import eventsourcing.domain.event.AccountCreated;
import eventsourcing.domain.event.AccountEvent;
import eventsourcing.domain.event.ClosureRejected;
import eventsourcing.domain.event.CreditRejected;
import eventsourcing.domain.event.Credited;
import eventsourcing.domain.event.DebitConfirmed;
import eventsourcing.domain.event.Debited;
import eventsourcing.domain.event.NameChanged;
import eventsourcing.domain.state.Account;
import eventsourcing.domain.state.ClosedAccount;
import eventsourcing.domain.state.OpenAccount;

import java.util.function.Supplier;

public record AccountDecider(Supplier<Id> idSupplier, Supplier<Long> timeSupplier) implements Decider {

  @Override
  public Event decide(Command command, EventMeta eventMeta) {
    if (command instanceof AccountCommand accountCommand) {
      return decide(accountCommand, eventMeta);
    } else {
      throw UnknownCommandRTE.of(command);
    }
  }

  @Override
  public Event decide(State state, Command command, EventMeta eventMeta) throws CommandException {
    if (state instanceof Account account) {
      if (command instanceof AccountCommand accountCommand) {
        return decide(account, accountCommand, eventMeta);
      } else {
        throw UnknownCommandRTE.of(command);
      }
    } else {
      throw UnknownState.of(state);
    }
  }

  @SuppressWarnings("SwitchStatementWithTooFewBranches")
  private AccountEvent decide(AccountCommand command, EventMeta newEventMeta) {
    return switch (command) {
      case CreateAccount cmd -> new AccountCreated(newEventMeta, cmd.accountName(), cmd.balance());
      default -> throw UnknownCommandRTE.of(command);
    };
  }

  private AccountEvent decide(Account state, AccountCommand command, EventMeta newEventMeta)
          throws MismatchingCommandState, InvalidCommand {
    return switch (state) {
      case OpenAccount openAccount -> decide(openAccount, command, newEventMeta);
      case ClosedAccount acc -> decide(acc, command, newEventMeta);
    };
  }

  private AccountEvent decide(OpenAccount account, AccountCommand command, EventMeta meta) throws InvalidCommand {
    return switch (command) {
      case CreateAccount cmd -> throw InvalidCommand.ofEvolution(account, cmd);
      case ChangeName cmd -> new NameChanged(meta, cmd.name());
      case Debit cmd -> tryToDebit(cmd, account, meta);
      case Credit cmd -> new Credited(meta, cmd.debitedAcc(), cmd.amount());
      case ConfirmDebit _ -> new DebitConfirmed(meta);
      case CloseAccount _ -> tryToClose(account, meta);
    };
  }

  private AccountEvent decide(ClosedAccount state, AccountCommand command, EventMeta meta) throws InvalidCommand {
    return switch (command) {
      case Credit cmd -> new CreditRejected(meta, cmd.debitedAcc(), cmd.amount());
      case ConfirmDebit _ -> new DebitConfirmed(meta);
      case ChangeName cmd -> throw InvalidCommand.ofEvolution(state, cmd);
      case Debit cmd -> throw InvalidCommand.ofEvolution(state, cmd);
      case CreateAccount cmd -> throw InvalidCommand.ofEvolution(state, cmd);
      case CloseAccount cmd -> throw InvalidCommand.ofEvolution(state, cmd);
    };
  }

  private static AccountEvent tryToDebit(Debit cmd, OpenAccount account, EventMeta meta) {
    if (account.canDebit(cmd.amount())) {
      return new Debited(meta, cmd.creditedAcc(), cmd.amount());
    } else {
      return new DebitRejected(meta);
    }
  }

  private static AccountEvent tryToClose(OpenAccount openAccount, EventMeta meta) {
    if (openAccount.hasOngoingDebit()) {
      return new ClosureRejected(meta);
    } else {
      return new AccountClosed(meta);
    }
  }
}
