package io.memoria.atom.eventsourcing.usecase.banking;

import io.memoria.atom.core.id.Id;
import io.memoria.atom.eventsourcing.usecase.banking.command.*;
import io.vavr.collection.List;

import java.util.Random;

class DataSet {
  public static final String NAME_PREFIX = "name_version:";

  static Id accountId(int i) {
    return Id.of("acc_id_" + i);
  }

  static String createName(int nameVersion) {
    return NAME_PREFIX + nameVersion;
  }

  static List<AccountCommand> createAccounts(int nAccounts, int balance) {
    return List.range(0, nAccounts).map(i -> CreateAccount.of(accountId(i), createName(i), balance));
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

  public static List<AccountCommand> changeName(int nAccounts, int version) {
    return List.range(0, nAccounts).map(i -> new ChangeName(accountId(i), Id.of(), createName(version)));
  }

  public static List<AccountCommand> credit(int nAccounts, int balance) {
    return List.range(0, nAccounts).map(i -> new Credit(Id.of(), accountId(i), Id.of("SomeFakeDebitId"), balance));
  }

  /**
   * Send money from first half to second half of accounts
   */
  public static List<AccountCommand> debit(int nAccounts, int balance) {
    int maxDebitIds = nAccounts / 2;
    return List.range(0, maxDebitIds).map(i -> new Debit(Id.of(), accountId(i), accountId(nAccounts - i - 1), balance));
  }

  private static AccountCommand createOutboundBalance(Id from, Id to, int amount) {
    return Debit.of(from, to, amount);
  }

  private static List<Id> shuffledIds(int nAccounts) {
    return List.range(0, nAccounts).shuffle().map(DataSet::accountId);
  }

  private DataSet() {}
}
