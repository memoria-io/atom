package io.memoria.atom.es.active.banking.command;

import io.memoria.atom.core.eventsourcing.CommandId;
import io.memoria.atom.core.eventsourcing.StateId;
import io.memoria.atom.es.active.banking.state.Transfer;

public record HandleInboundTransfer(CommandId commandId, StateId stateId, Transfer transfer) implements AccountCommand {
  public static HandleInboundTransfer of(StateId stateId, Transfer transfer) {
    return new HandleInboundTransfer(CommandId.randomUUID(), stateId, transfer);
  }
}