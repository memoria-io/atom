package io.memoria.atom.eventsourcing.usecase.banking.state;

import io.memoria.atom.eventsourcing.StateId;

public record Acc(StateId accountId, int seqId, String name, int balance, int debitCount) implements Account {

  public boolean hasOngoingDebit() {
    return debitCount != 0;
  }

  public Acc withCredit(int seqId, int credit) {
    return new Acc(accountId, seqId, name, balance + credit, debitCount);
  }

  public Acc withDebit(int seqId, int debit) {
    return new Acc(accountId, seqId, name, balance - debit, debitCount + 1);
  }

  public Acc withDebitConfirmed(int seqId) {
    return new Acc(accountId, seqId, name, balance, debitCount - 1);
  }

  public Acc withDebitRejected(int seqId, int returnedDebit) {
    return new Acc(accountId, seqId, name, balance + returnedDebit, debitCount - 1);
  }

  public Acc withName(int seqId, String newName) {
    return new Acc(accountId, seqId, newName, balance, debitCount);
  }
}
