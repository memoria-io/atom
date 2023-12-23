package io.memoria.atom.eventsourcing.exceptions;

import io.memoria.atom.eventsourcing.Command;
import io.memoria.atom.eventsourcing.Event;
import io.memoria.atom.eventsourcing.State;

public class InvalidEvolution extends IllegalArgumentException implements ESException {

  protected InvalidEvolution(String msg) {
    super(msg);
  }

  public static InvalidEvolution of(Event event) {
    var msg = "Invalid creator event: %s[%s] for creating state";
    return new InvalidEvolution(msg.formatted(event.getClass().getSimpleName(), event.meta()));
  }

  public static InvalidEvolution of(Event event, State state) {
    var msg = "Invalid evolution event: %s[%s] to the state: %s[%s]";
    return new InvalidEvolution(msg.formatted(event.getClass().getSimpleName(),
                                              event.meta(),
                                              state.getClass().getSimpleName(),
                                              state.meta()));
  }

  public static InvalidEvolution of(Command command) {
    var msg = "Invalid creator command (%s)".formatted(command.getClass().getSimpleName());
    return new InvalidEvolution(msg);
  }

  public static InvalidEvolution of(Command command, State state) {
    var msg = "Invalid command (%s) for the state (%s)".formatted(command.getClass().getSimpleName(),
                                                                  state.getClass().getSimpleName());
    return new InvalidEvolution(msg);
  }
}
