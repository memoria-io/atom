package io.memoria.atom.eventsourcing.exception;

import io.memoria.atom.eventsourcing.Command;
import io.memoria.atom.eventsourcing.Event;
import io.memoria.atom.eventsourcing.State;

/**
 * Eventsourcing Exception
 */
public interface ESException {

  class CorruptedStream extends IllegalArgumentException implements ESException {
    private static final String msg = "CorruptedStream operation: %s on current state: %s, this should never happen";

    private CorruptedStream(State state, Event event) {
      super(msg.formatted(state.getClass().getSimpleName(), event.getClass().getSimpleName()));
    }

    public static CorruptedStream of(State state, Event event) {
      return new CorruptedStream(state, event);
    }
  }

  class InvalidStream extends Exception implements ESException {
    private InvalidStream(Command command) {
      super("Invalid stream, stateID doesn't match assigned partition: %s ".formatted(command));
    }

    public static InvalidStream create(Command command) {
      return new InvalidStream(command);
    }
  }
}
