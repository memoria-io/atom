package io.memoria.atom.eventsourcing;

import io.memoria.atom.core.domain.Shardable;
import io.memoria.atom.eventsourcing.command.Command;
import io.memoria.atom.eventsourcing.command.exceptions.UnknownCommand;
import io.memoria.atom.eventsourcing.event.Event;
import io.memoria.atom.eventsourcing.event.exceptions.UnknownEvent;
import io.memoria.atom.eventsourcing.state.State;
import io.memoria.atom.eventsourcing.state.exceptions.UnknownState;
import io.vavr.Tuple;
import io.vavr.Tuple2;


public class Validations {

  private Validations() {}

  public static Try<Shardable> instanceOf(Shardable shardable, boolean condition) {
    if (condition) {
      return Try.success(shardable);
    } else {
      var ex = switch (shardable) {
        case Command cmd -> UnknownCommand.of(cmd);
        case State state -> UnknownState.of(state);
        case Event event -> UnknownEvent.of(event);
        default -> new IllegalArgumentException("Unknown shardable %s".formatted(shardable));
      };
      return Try.failure(ex);
    }
  }

  @SuppressWarnings("unchecked")
  public static <T extends Shardable> Try<T> instanceOf(Shardable shardable, Class<T> tClass) {
    return instanceOf(shardable, tClass.isAssignableFrom(shardable.getClass())).map(t -> (T) t);
  }

  public static <S extends State, E extends Event> Try<Tuple2<S, E>> instanceOf(State state,
                                                                                Class<S> sClass,
                                                                                Event event,
                                                                                Class<E> eClass) {
    return instanceOf(state, sClass).flatMap(s -> instanceOf(event, eClass).map(e -> Tuple.of(s, e)));
  }

  public static <S extends State, C extends Command> Try<Tuple2<S, C>> instanceOf(State state,
                                                                                  Class<S> sClass,
                                                                                  Command command,
                                                                                  Class<C> cClass) {
    return instanceOf(state, sClass).flatMap(s -> instanceOf(command, cClass).map(c -> Tuple.of(s, c)));
  }
}
