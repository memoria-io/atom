package io.memoria.atom.eventsourcing.usecase.banking.event;

import io.memoria.atom.core.id.Id;
import io.memoria.atom.eventsourcing.usecase.banking.command.CloseAccount;
import io.memoria.atom.eventsourcing.usecase.banking.state.Account;

public record ClosureRejected(Id eventId, int seqId, Id commandId, Id accountId) implements AccountEvent {
  @Override
  public Id stateId() {
    return accountId;
  }

  public static ClosureRejected from(Account account, CloseAccount cmd) {
    return new ClosureRejected(Id.of(), account.seqId() + 1, cmd.commandId(), cmd.stateId());
  }
}
