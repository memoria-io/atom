package io.memoria.atom.es.active.banking.state;

import io.memoria.atom.core.eventsourcing.StateId;
import io.memoria.atom.es.active.banking.event.InboundTransferAccepted;
import io.memoria.atom.es.active.banking.event.InboundTransferRejected;
import io.memoria.atom.es.active.banking.event.OutboundTransferAccepted;
import io.memoria.atom.es.active.banking.event.OutboundTransferRejected;
import io.memoria.atom.es.active.banking.event.TransferCreated;

public record ActiveAccount(StateId stateId, String name, int balance, int ongoingTransactions) implements User {
  public boolean isClosable() {
    return ongoingTransactions == 0;
  }

  public ActiveAccount with(TransferCreated event) {
    return new ActiveAccount(stateId, name, balance - event.transfer().amount(), ongoingTransactions + 1);
  }

  public ActiveAccount with(InboundTransferAccepted event) {
    return new ActiveAccount(stateId, name, balance + event.transfer().amount(), ongoingTransactions);
  }

  public ActiveAccount with(InboundTransferRejected event) {
    return this;
  }

  public ActiveAccount with(OutboundTransferAccepted event) {
    return new ActiveAccount(stateId, name, balance, ongoingTransactions - 1);
  }

  public ActiveAccount with(OutboundTransferRejected event) {
    return new ActiveAccount(stateId, name, balance, ongoingTransactions - 1);
  }
}
