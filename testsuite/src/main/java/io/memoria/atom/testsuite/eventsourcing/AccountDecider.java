package io.memoria.atom.testsuite.eventsourcing;

import io.memoria.atom.core.id.Id;
import io.memoria.atom.eventsourcing.Command;
import io.memoria.atom.eventsourcing.Event;
import io.memoria.atom.eventsourcing.EventMeta;
import io.memoria.atom.eventsourcing.State;
import io.memoria.atom.eventsourcing.exceptions.InvalidEvolution;
import io.memoria.atom.eventsourcing.rule.Decider;
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
import io.vavr.control.Try;

import java.util.function.Supplier;

import static io.memoria.atom.eventsourcing.Validations.instanceOf;
import static io.vavr.control.Try.failure;
import static io.vavr.control.Try.success;

public record AccountDecider(Supplier<Id> idSupplier, Supplier<Long> timeSupplier) implements Decider {

  @Override
  public Try<Event> apply(Command c) {
    return instanceOf(c, AccountCommand.class).flatMap(this::handle);
  }

  @Override
  public Try<Event> apply(State state, Command command) {
    return instanceOf(state, Account.class, command, AccountCommand.class).flatMap(tup -> handle(tup._1, tup._2));
  }

  @SuppressWarnings("SwitchStatementWithTooFewBranches")
  private Try<AccountEvent> handle(AccountCommand command) {
    return eventMeta(command).flatMap(meta -> switch (command) {
      case CreateAccount cmd -> success(new AccountCreated(meta, cmd.accountName(), cmd.balance()));
      default -> failure(InvalidEvolution.of(command));
    });
  }

  private Try<AccountEvent> handle(Account state, AccountCommand command) {
    return eventMeta(state, command).flatMap(meta -> switch (state) {
      case OpenAccount openAccount -> handle(openAccount, command, meta);
      case ClosedAccount acc -> handle(acc, command, meta);
    });
  }

  private Try<AccountEvent> handle(OpenAccount account, AccountCommand command, EventMeta meta) {
    return switch (command) {
      case CreateAccount cmd -> failure(InvalidEvolution.of(cmd, account));
      case ChangeName cmd -> success(new NameChanged(meta, cmd.name()));
      case Debit cmd -> tryToDebit(cmd, account, meta);
      case Credit cmd -> success(new Credited(meta, cmd.debitedAcc(), cmd.amount()));
      case ConfirmDebit _ -> success(new DebitConfirmed(meta));
      case CloseAccount _ -> tryToClose(account, meta);
    };
  }

  private Try<AccountEvent> handle(ClosedAccount state, AccountCommand command, EventMeta meta) {
    return switch (command) {
      case Credit cmd -> success(new CreditRejected(meta, cmd.debitedAcc(), cmd.amount()));
      case ConfirmDebit _ -> success(new DebitConfirmed(meta));
      case ChangeName cmd -> failure(InvalidEvolution.of(cmd, state));
      case Debit cmd -> failure(InvalidEvolution.of(cmd, state));
      case CreateAccount cmd -> failure(InvalidEvolution.of(cmd, state));
      case CloseAccount cmd -> failure(InvalidEvolution.of(cmd, state));
    };
  }

  private static Try<AccountEvent> tryToDebit(Debit cmd, OpenAccount account, EventMeta meta) {
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
