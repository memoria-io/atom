package io.memoria.atom.es.active;

import io.memoria.atom.core.eventsourcing.Command;
import io.memoria.atom.core.eventsourcing.Event;
import io.memoria.atom.core.eventsourcing.State;
import io.memoria.atom.core.eventsourcing.exception.ESException;
import io.vavr.control.Try;

public class Utils {
  private Utils() {}

  public static <E extends Event> Try<E> invalidOperation(State state, Command command) {
    return Try.failure(ESException.InvalidCommand.create(state, command));
  }
}
