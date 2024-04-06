package io.memoria.atom.eventsourcing.event.exceptions;

import io.memoria.atom.eventsourcing.ESException;
import io.memoria.atom.eventsourcing.event.Event;

public interface EventException extends ESException {
  Event event();
}
