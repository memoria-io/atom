package io.memoria.atom.core.utils;

import io.vavr.API;
import io.vavr.Predicates;
import io.vavr.collection.List;
import io.vavr.collection.Traversable;
import io.vavr.control.Option;
import io.vavr.control.Try;

import java.util.function.BiFunction;
import java.util.function.Function;

public final class Vavrs {
  private Vavrs() {}

  public static <V> BiFunction<V, Throwable, Try<V>> handle() {
    return (v, t) -> (t == null) ? Try.success(v) : Try.failure(t);
  }

  public static <V> BiFunction<V, Throwable, Try<Void>> handleToVoid() {
    return (v, t) -> (t == null) ? Try.success(null) : Try.failure(t);
  }

  public static <T, R> API.Match.Case<T, R> instanceOfCase(Class<?> c, R r) {
    return API.Case(API.$(Predicates.instanceOf(c)), () -> r);
  }

  public static <T> List<Try<T>> listOfTry(Try<List<T>> tt) {
    if (tt.isSuccess())
      return tt.get().map(Try::success);
    else
      return List.of(Try.failure(tt.getCause()));
  }

  public static <A extends Traversable<B>, B> Traversable<Try<B>> traverseOfTry(Try<A> tt) {
    if (tt.isSuccess())
      return tt.get().map(Try::success);
    else
      return List.of(Try.failure(tt.getCause()));
  }

  public static <T, E> Function<Option<T>, Try<Option<E>>> optToTryOpt(Function<T, Try<E>> fn) {
    return opt -> optToTryOpt(opt, fn);
  }

  public static <T, E> Try<Option<E>> optToTryOpt(Option<T> option, Function<T, Try<E>> fn) {
    if (option.isEmpty()) {
      return Try.success(Option.none());
    } else {
      return fn.apply(option.get()).map(Option::some);
    }
  }

  public static <T> Try<List<T>> toListOfTry(List<Try<T>> list) {
    return Try.of(() -> list.map(Try::get));
  }
}
