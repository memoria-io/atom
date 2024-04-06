package io.memoria.atom.eventsourcing.state.exceptions;

import io.memoria.atom.eventsourcing.ESException;
import io.memoria.atom.eventsourcing.state.State;

public interface StateException extends ESException {
  State state();
}
