package io.memoria.atom.eventsourcing;

import java.io.Serializable;

public interface State extends Serializable {
  StateId stateId();

  int seqId();
}
