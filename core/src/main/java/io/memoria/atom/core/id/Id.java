package io.memoria.atom.core.id;

import io.vavr.control.Try;

import java.io.Serializable;
import java.util.Objects;
import java.util.UUID;
import java.util.function.Function;

public class Id implements Serializable, Comparable<Id> {
  private final String value;

  public Id(String value) {
    if (value == null || value.isEmpty())
      throw new IllegalArgumentException("Id value is null or empty.");
    this.value = value;
  }

  public Id(long value) {
    if (value < 0) {
      throw new IllegalArgumentException("Sequence Id value is less than 0");
    }
    this.value = String.valueOf(value);
  }

  public Id(UUID uuid) {
    this(uuid.toString());
  }

  public Id(Id id) {
    this.value = id.value;
  }

  public String value() {
    return value;
  }

  public static <T extends Id> T to(String value, Function<Id, T> fn) {
    return fn.apply(Id.of(value));
  }

  public static <T extends Id> T to(long value, Function<Id, T> fn) {
    return fn.apply(Id.of(value));
  }

  public static <T extends Id> T to(UUID value, Function<Id, T> fn) {
    return fn.apply(Id.of(value));
  }

  public static Id of(String value) {
    return new Id(value);
  }

  public static Id of(long value) {
    return new Id(value);
  }

  public static Id of(UUID uuid) {
    return new Id(uuid.toString());
  }

  @Override
  public int compareTo(Id o) {
    return Try.of(() -> compareToLong(o)).orElse(Try.of(() -> compareToUuid(o))).getOrElse(compareToString(o));
  }

  @Override
  public boolean equals(Object o) {
    if (this == o)
      return true;
    if (o == null || getClass() != o.getClass())
      return false;
    Id id = (Id) o;
    return Objects.equals(value, id.value);
  }

  @Override
  public int hashCode() {
    return Objects.hash(value);
  }

  private int compareToString(Id o) {
    return value.compareTo(o.value);
  }

  private int compareToLong(Id o) {
    return Long.compare(Long.parseLong(value), Long.parseLong(o.value));
  }

  private int compareToUuid(Id o) {
    return UUID.fromString(value).compareTo(UUID.fromString(o.value));
  }

  @Override
  public String toString() {
    return value;
  }
}
