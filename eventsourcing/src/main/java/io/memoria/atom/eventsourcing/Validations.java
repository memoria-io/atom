package io.memoria.atom.eventsourcing;

import io.memoria.atom.core.Shardable;
import io.memoria.atom.eventsourcing.exceptions.UnknownImplementation;
import io.vavr.Tuple;
import io.vavr.Tuple2;
import io.vavr.control.Try;

public class Validations {

  private Validations() {}

  public static Try<Shardable> instanceOf(Shardable shardable, boolean condition) {
    return (condition) ? Try.success(shardable) : Try.failure(UnknownImplementation.of(shardable));
  }

  @SuppressWarnings("unchecked")
  public static <T extends Shardable> Try<T> instanceOf(Shardable shardable, Class<T> tClass) {
    return shardable.getClass().isInstance(tClass) ? Try.success((T) shardable)
                                                   : Try.failure(UnknownImplementation.of(shardable));
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
