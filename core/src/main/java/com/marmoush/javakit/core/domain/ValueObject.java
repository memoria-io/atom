package com.marmoush.javakit.core.domain;

@FunctionalInterface
public interface ValueObject<T> {
  T value();
}
