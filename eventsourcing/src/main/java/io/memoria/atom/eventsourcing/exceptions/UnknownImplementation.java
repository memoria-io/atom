package io.memoria.atom.eventsourcing.exceptions;

import io.memoria.atom.core.domain.Shardable;
import io.memoria.atom.eventsourcing.Command;
import io.memoria.atom.eventsourcing.Event;
import io.memoria.atom.eventsourcing.State;

public class UnknownImplementation extends IllegalArgumentException implements ESException {
  private static final String message = "Unknown %s: %s[%s] implementation";

  protected UnknownImplementation(String msg) {
    super(msg);
  }

  public static UnknownImplementation of(Shardable shardable) {
    var msg = switch (shardable) {
      case Command cmd -> message.formatted("Command", cmd.getClass().getSimpleName(), cmd.meta());
      case State state -> message.formatted("State", state.getClass().getSimpleName(), state.meta());
      case Event event -> message.formatted("Event", event.getClass().getSimpleName(), event.meta());
      default -> message.formatted("Shardable", shardable.getClass().getSimpleName(), "");
    };
    return new UnknownImplementation(msg);
  }
}
