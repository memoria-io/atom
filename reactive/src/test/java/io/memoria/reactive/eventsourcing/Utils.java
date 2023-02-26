package io.memoria.reactive.eventsourcing;

import io.memoria.atom.core.eventsourcing.Command;
import io.memoria.atom.core.eventsourcing.Event;
import io.memoria.atom.core.eventsourcing.State;
import io.memoria.atom.core.eventsourcing.exception.ESException.InvalidCommand;
import io.vavr.control.Try;

public class Utils {
  private Utils() {}

  public static <E extends Event> Try<E> error(State state, Command command) {
    return Try.failure(InvalidCommand.create(state, command));
  }
}
