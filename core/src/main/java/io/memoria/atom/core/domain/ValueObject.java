package io.memoria.atom.core.domain;

@FunctionalInterface
public interface ValueObject<T> {
  T value();
}
