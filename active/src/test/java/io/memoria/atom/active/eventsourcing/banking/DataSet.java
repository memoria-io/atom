package io.memoria.atom.active.eventsourcing.banking;

import io.memoria.atom.active.eventsourcing.banking.command.CloseAccount;
import io.memoria.atom.active.eventsourcing.banking.command.CreateAccount;
import io.memoria.atom.active.eventsourcing.banking.command.CreateTransfer;
import io.memoria.atom.active.eventsourcing.banking.command.UserCommand;
import io.memoria.atom.active.eventsourcing.banking.state.Transfer;
import io.memoria.atom.core.eventsourcing.StateId;
import io.memoria.atom.core.id.Id;
import io.vavr.collection.List;

import java.util.Random;

class DataSet {
  private DataSet() {}

  static StateId createId(int i) {
    return StateId.of("acc_id_" + i);
  }

  static String createName(int i) {
    return "name_" + i;
  }

  static String createNewName(int i) {
    return "new_name_" + i;
  }

  static List<UserCommand> createAccounts(int nAccounts, int balance) {
    return List.range(0, nAccounts).map(i -> CreateAccount.of(createId(i), createName(i), balance));
  }

  static List<UserCommand> randomClosure(int nAccounts) {
    return shuffledIds(nAccounts).map(CloseAccount::of);
  }

  static List<UserCommand> randomOutBounds(int nAccounts, int maxAmount) {
    var accounts = shuffledIds(nAccounts);
    var from = accounts.subSequence(0, nAccounts / 2);
    var to = accounts.subSequence(nAccounts / 2, nAccounts);
    var amounts = List.ofAll(new Random().ints(nAccounts, 1, maxAmount).boxed());
    return List.range(0, nAccounts / 2).map(i -> createTransfer(from.get(i), to.get(i), amounts.get(i)));
  }

  static UserCommand createTransfer(StateId sender, StateId receiver, int amount) {
    var transaction = new Transfer(Id.of(), sender, receiver, amount);
    return CreateTransfer.of(transaction);
  }

  private static List<StateId> shuffledIds(int nAccounts) {
    return List.range(0, nAccounts).shuffle().map(DataSet::createId);
  }
}
