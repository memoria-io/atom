package io.memoria.atom.eventsourcing;

import io.memoria.atom.core.domain.Shardable;
import io.memoria.atom.eventsourcing.command.Command;
import io.memoria.atom.eventsourcing.command.exceptions.UnknownCommand;
import io.memoria.atom.eventsourcing.event.Event;
import io.memoria.atom.eventsourcing.event.exceptions.UnknownEvent;
import io.memoria.atom.eventsourcing.state.State;
import io.memoria.atom.eventsourcing.state.exceptions.UnknownState;

public class Validations {

  private Validations() {}

  public static Shardable instanceOf(Shardable shardable, boolean condition) throws ESException {
    if (condition) {
      return shardable;
    } else {
      throw switch (shardable) {
        case Command cmd -> UnknownCommand.of(cmd);
        case State state -> UnknownState.of(state);
        case Event event -> UnknownEvent.of(event);
        default -> new ESException("Unknown shardable %s".formatted(shardable));
      };
    }
  }

  @SuppressWarnings("unchecked")
  public static <T extends Shardable> T instanceOf(Shardable shardable, Class<T> tClass) throws ESException {
    return (T) instanceOf(shardable, tClass.isAssignableFrom(shardable.getClass()));
  }
}
