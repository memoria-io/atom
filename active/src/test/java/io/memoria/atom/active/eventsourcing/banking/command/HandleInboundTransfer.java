package io.memoria.atom.active.eventsourcing.banking.command;

import io.memoria.atom.active.eventsourcing.banking.state.Transfer;
import io.memoria.atom.core.eventsourcing.CommandId;
import io.memoria.atom.core.eventsourcing.StateId;

public record HandleInboundTransfer(CommandId commandId, StateId stateId, Transfer transfer) implements AccountCommand {
  public static HandleInboundTransfer of(StateId stateId, Transfer transfer) {
    return new HandleInboundTransfer(CommandId.randomUUID(), stateId, transfer);
  }
}
