package io.memoria.atom.testsuite.eventsourcing;

import io.memoria.atom.core.id.Id;
import io.memoria.atom.eventsourcing.ESException;
import io.memoria.atom.eventsourcing.command.Command;
import io.memoria.atom.eventsourcing.command.exceptions.InvalidEvolutionCommand;
import io.memoria.atom.eventsourcing.command.exceptions.MismatchingCommandState;
import io.memoria.atom.eventsourcing.command.exceptions.UnknownCommand;
import io.memoria.atom.eventsourcing.event.Event;
import io.memoria.atom.eventsourcing.event.EventMeta;
import io.memoria.atom.eventsourcing.rule.Decider;
import io.memoria.atom.eventsourcing.state.State;
import io.memoria.atom.eventsourcing.state.exceptions.UnknownState;
import io.memoria.atom.testsuite.eventsourcing.command.AccountCommand;
import io.memoria.atom.testsuite.eventsourcing.command.ChangeName;
import io.memoria.atom.testsuite.eventsourcing.command.CloseAccount;
import io.memoria.atom.testsuite.eventsourcing.command.ConfirmDebit;
import io.memoria.atom.testsuite.eventsourcing.command.CreateAccount;
import io.memoria.atom.testsuite.eventsourcing.command.Credit;
import io.memoria.atom.testsuite.eventsourcing.command.Debit;
import io.memoria.atom.testsuite.eventsourcing.event.AccountClosed;
import io.memoria.atom.testsuite.eventsourcing.event.AccountCreated;
import io.memoria.atom.testsuite.eventsourcing.event.AccountEvent;
import io.memoria.atom.testsuite.eventsourcing.event.ClosureRejected;
import io.memoria.atom.testsuite.eventsourcing.event.CreditRejected;
import io.memoria.atom.testsuite.eventsourcing.event.Credited;
import io.memoria.atom.testsuite.eventsourcing.event.DebitConfirmed;
import io.memoria.atom.testsuite.eventsourcing.event.DebitRejected;
import io.memoria.atom.testsuite.eventsourcing.event.Debited;
import io.memoria.atom.testsuite.eventsourcing.event.NameChanged;
import io.memoria.atom.testsuite.eventsourcing.state.Account;
import io.memoria.atom.testsuite.eventsourcing.state.ClosedAccount;
import io.memoria.atom.testsuite.eventsourcing.state.OpenAccount;

import java.util.function.Supplier;

public record AccountDecider(Supplier<Id> idSupplier, Supplier<Long> timeSupplier) implements Decider {

  @Override
  public Event apply(Command command) {
    if (command instanceof AccountCommand accountCommand) {
      return handle(accountCommand);
    } else {
      throw UnknownCommand.of(command);
    }
  }

  @Override
  public Event apply(State state, Command command) throws ESException {
    if (state instanceof Account account) {
      if (command instanceof AccountCommand accountCommand) {
        return handle(account, accountCommand);
      } else {
        throw UnknownCommand.of(command);
      }
    } else {
      throw UnknownState.of(state);
    }
  }

  @SuppressWarnings("SwitchStatementWithTooFewBranches")
  private AccountEvent handle(AccountCommand command) {
    var meta = eventMeta(command);
    return switch (command) {
      case CreateAccount cmd -> new AccountCreated(meta, cmd.accountName(), cmd.balance());
      default -> throw UnknownCommand.of(command);
    };
  }

  private AccountEvent handle(Account state, AccountCommand command)
          throws MismatchingCommandState, InvalidEvolutionCommand {
    var meta = eventMeta(state, command);
    return switch (state) {
      case OpenAccount openAccount -> handle(openAccount, command, meta);
      case ClosedAccount acc -> handle(acc, command, meta);
    };
  }

  private AccountEvent handle(OpenAccount account, AccountCommand command, EventMeta meta)
          throws InvalidEvolutionCommand {
    return switch (command) {
      case CreateAccount cmd -> throw InvalidEvolutionCommand.of(cmd, account);
      case ChangeName cmd -> new NameChanged(meta, cmd.name());
      case Debit cmd -> tryToDebit(cmd, account, meta);
      case Credit cmd -> new Credited(meta, cmd.debitedAcc(), cmd.amount());
      case ConfirmDebit _ -> new DebitConfirmed(meta);
      case CloseAccount _ -> tryToClose(account, meta);
    };
  }

  private AccountEvent handle(ClosedAccount state, AccountCommand command, EventMeta meta)
          throws InvalidEvolutionCommand {
    return switch (command) {
      case Credit cmd -> new CreditRejected(meta, cmd.debitedAcc(), cmd.amount());
      case ConfirmDebit _ -> new DebitConfirmed(meta);
      case ChangeName cmd -> throw InvalidEvolutionCommand.of(cmd, state);
      case Debit cmd -> throw InvalidEvolutionCommand.of(cmd, state);
      case CreateAccount cmd -> throw InvalidEvolutionCommand.of(cmd, state);
      case CloseAccount cmd -> throw InvalidEvolutionCommand.of(cmd, state);
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
