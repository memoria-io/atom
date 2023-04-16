package io.memoria.atom.eventsourcing.usecase.banking.event;

import io.memoria.atom.core.id.Id;
import io.memoria.atom.eventsourcing.usecase.banking.command.ChangeName;
import io.memoria.atom.eventsourcing.usecase.banking.state.Account;

public record NameChanged(Id eventId, Id commandId, Id accountId, String newName) implements AccountEvent {
  @Override
  public Id stateId() {
    return accountId;
  }

  public static NameChanged from(Account account, ChangeName cmd) {
    return new NameChanged(Id.of(), cmd.commandId(), cmd.stateId(), cmd.name());
  }
}
