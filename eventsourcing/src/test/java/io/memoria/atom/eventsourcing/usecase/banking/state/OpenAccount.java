package io.memoria.atom.eventsourcing.usecase.banking.state;

import io.memoria.atom.core.id.Id;

public record OpenAccount(Id accountId, int seqId, String name, int balance, int debitCount) implements Account {

  public boolean hasOngoingDebit() {
    return debitCount != 0;
  }

  public OpenAccount withCredit(int seqId, int credit) {
    return new OpenAccount(accountId, seqId, name, balance + credit, debitCount);
  }

  public OpenAccount withDebit(int seqId, int debit) {
    return new OpenAccount(accountId, seqId, name, balance - debit, debitCount + 1);
  }

  public OpenAccount withDebitConfirmed(int seqId) {
    return new OpenAccount(accountId, seqId, name, balance, debitCount - 1);
  }

  public OpenAccount withDebitRejected(int seqId, int returnedDebit) {
    return new OpenAccount(accountId, seqId, name, balance + returnedDebit, debitCount - 1);
  }

  public OpenAccount withName(int seqId, String newName) {
    return new OpenAccount(accountId, seqId, newName, balance, debitCount);
  }
}
