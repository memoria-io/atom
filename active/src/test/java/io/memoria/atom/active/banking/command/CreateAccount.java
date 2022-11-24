package io.memoria.atom.active.banking.command;

import io.memoria.atom.core.eventsourcing.CommandId;
import io.memoria.atom.core.eventsourcing.StateId;

public record CreateAccount(CommandId commandId, StateId stateId, String accountName, int balance)
        implements AccountCommand {
  public static CreateAccount of(StateId stateId, String accountName, int balance) {
    return new CreateAccount(CommandId.randomUUID(), stateId, accountName, balance);
  }
}
