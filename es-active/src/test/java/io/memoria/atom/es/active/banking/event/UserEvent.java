package io.memoria.atom.es.active.banking.event;

import io.memoria.atom.core.eventsourcing.Event;

public sealed interface UserEvent extends Event permits AccountClosed,
                                                        AccountCreated, OutboundTransferRejected,
                                                        OutboundTransferAccepted,
                                                        TransferCreated,
                                                        InboundTransferAccepted,
                                                        InboundTransferRejected {

  @Override
  default long timestamp() {
    return 0;
  }
}
