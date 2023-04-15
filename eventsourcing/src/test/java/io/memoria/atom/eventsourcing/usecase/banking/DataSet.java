package io.memoria.atom.eventsourcing.usecase.banking;

import io.memoria.atom.eventsourcing.CommandId;
import io.memoria.atom.eventsourcing.StateId;
import io.memoria.atom.eventsourcing.usecase.banking.command.*;
import io.vavr.collection.List;
import reactor.core.publisher.Flux;

import java.util.Random;

class DataSet {
  private DataSet() {}

  static Flux<AccountCommand> scenario(int nAccounts, int nameChanges) {
    var createAccounts = createAccounts(nAccounts, 0);
    var changes = List.range(0, nameChanges).flatMap(version -> changeName(nAccounts, version));
    return Flux.fromIterable(createAccounts.appendAll(changes));
  }

  static StateId accountStateId(int i) {
    return StateId.of("acc_id_" + i);
  }

  static String createName(int i) {
    return "name_version:" + i;
  }

  static String createNewName(int i) {
    return "new_name_version:" + i;
  }

  static List<AccountCommand> createAccounts(int nAccounts, int balance) {
    return List.range(0, nAccounts).map(i -> CreateAccount.of(accountStateId(i), createName(i), balance));
  }

  static List<AccountCommand> randomClosure(int nAccounts) {
    return shuffledIds(nAccounts).map(CloseAccount::of);
  }

  static List<AccountCommand> randomOutBounds(int nAccounts, int maxAmount) {
    var accounts = shuffledIds(nAccounts);
    var from = accounts.subSequence(0, nAccounts / 2);
    var to = accounts.subSequence(nAccounts / 2, nAccounts);
    var amounts = List.ofAll(new Random().ints(nAccounts, 1, maxAmount).boxed());
    return List.range(0, nAccounts / 2).map(i -> createOutboundBalance(from.get(i), to.get(i), amounts.get(i)));
  }

  public static List<ChangeName> changeName(int nAccounts, int version ) {
    return List.range(0, nAccounts).map(i -> new ChangeName(accountStateId(i), CommandId.randomUUID(), createNewName(version)));
  }

  private static AccountCommand createOutboundBalance(StateId from, StateId to, int amount) {
    return Debit.of(from, to, amount);
  }

  private static List<StateId> shuffledIds(int nAccounts) {
    return List.range(0, nAccounts).shuffle().map(DataSet::accountStateId);
  }
}
