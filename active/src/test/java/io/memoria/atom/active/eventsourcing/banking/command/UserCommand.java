package io.memoria.atom.active.eventsourcing.banking.command;

import io.memoria.atom.core.eventsourcing.Command;

public sealed interface UserCommand extends Command
        permits CloseAccount, CreateAccount, MarkAsRejected, MarkAsSuccessful, HandleInboundTransfer, CreateTransfer {
  @Override
  default long timestamp() {
    return 0;
  }
}
