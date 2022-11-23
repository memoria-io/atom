package io.memoria.reactive.eventsourcing.banking.command;

import io.memoria.atom.core.eventsourcing.CommandId;
import io.memoria.atom.core.eventsourcing.StateId;

public record CreateAccount(CommandId commandId, StateId accountId, String accountname, int balance)
        implements AccountCommand {
  public static CreateAccount of(StateId accountId, String accountname, int balance) {
    return new CreateAccount(CommandId.randomUUID(), accountId, accountname, balance);
  }
}
