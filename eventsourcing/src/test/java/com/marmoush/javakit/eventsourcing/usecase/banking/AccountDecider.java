package com.marmoush.javakit.eventsourcing.usecase.banking;

import com.marmoush.javakit.core.id.Id;
import com.marmoush.javakit.eventsourcing.aggregate.Decider;
import com.marmoush.javakit.eventsourcing.command.Command;
import com.marmoush.javakit.eventsourcing.command.exceptions.CommandException;
import com.marmoush.javakit.eventsourcing.command.exceptions.InvalidCommand;
import com.marmoush.javakit.eventsourcing.command.exceptions.MismatchingCommandState;
import com.marmoush.javakit.eventsourcing.command.exceptions.UnknownCommandRTE;
import com.marmoush.javakit.eventsourcing.event.Event;
import com.marmoush.javakit.eventsourcing.event.EventMeta;
import com.marmoush.javakit.eventsourcing.state.State;
import com.marmoush.javakit.eventsourcing.state.exceptions.UnknownState;
import com.marmoush.javakit.eventsourcing.usecase.banking.command.AccountCommand;
import com.marmoush.javakit.eventsourcing.usecase.banking.command.ChangeName;
import com.marmoush.javakit.eventsourcing.usecase.banking.command.CloseAccount;
import com.marmoush.javakit.eventsourcing.usecase.banking.command.ConfirmDebit;
import com.marmoush.javakit.eventsourcing.usecase.banking.command.CreateAccount;
import com.marmoush.javakit.eventsourcing.usecase.banking.command.Credit;
import com.marmoush.javakit.eventsourcing.usecase.banking.command.Debit;
import com.marmoush.javakit.eventsourcing.usecase.banking.event.AccountClosed;
import com.marmoush.javakit.eventsourcing.usecase.banking.event.AccountCreated;
import com.marmoush.javakit.eventsourcing.usecase.banking.event.AccountEvent;
import com.marmoush.javakit.eventsourcing.usecase.banking.event.ClosureRejected;
import com.marmoush.javakit.eventsourcing.usecase.banking.event.CreditRejected;
import com.marmoush.javakit.eventsourcing.usecase.banking.event.Credited;
import com.marmoush.javakit.eventsourcing.usecase.banking.event.DebitConfirmed;
import com.marmoush.javakit.eventsourcing.usecase.banking.event.DebitRejected;
import com.marmoush.javakit.eventsourcing.usecase.banking.event.Debited;
import com.marmoush.javakit.eventsourcing.usecase.banking.event.NameChanged;
import com.marmoush.javakit.eventsourcing.usecase.banking.state.Account;
import com.marmoush.javakit.eventsourcing.usecase.banking.state.ClosedAccount;
import com.marmoush.javakit.eventsourcing.usecase.banking.state.OpenAccount;

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
