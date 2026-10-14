package com.marmoush.atom.core.domain;

@FunctionalInterface
public interface ValueObject<T> {
  T value();
}
