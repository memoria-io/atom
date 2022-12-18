package io.memoria.atom.core.text;

import io.vavr.Function1;
import io.vavr.control.Try;

public interface TextTransformer {
  default <T> Function1<String, Try<T>> deserialize(Class<T> tClass) {
    return str -> deserialize(str, tClass);
  }

  <T> Try<T> deserialize(String str, Class<T> tClass);

  default <T> Function1<T, Try<String>> serialize() {
    return this::serialize;
  }

  <T> Try<String> serialize(T t);
}
