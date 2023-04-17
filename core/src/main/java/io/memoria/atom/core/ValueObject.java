package io.memoria.atom.core;

@FunctionalInterface
public interface ValueObject<T> {
  T value();
}
