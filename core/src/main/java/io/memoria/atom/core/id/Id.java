package io.memoria.atom.core.id;

import java.io.Serializable;
import java.util.Objects;
import java.util.UUID;

public class Id implements Serializable, Comparable<Id> {
  private final String value;

  public Id(String value) {
    Objects.requireNonNull(value);
    if (value.isEmpty())
      throw new IllegalArgumentException("Id value is empty.");
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

  @Override
  public int compareTo(Id o) {
    try {
      return compareToLong(o);
    } catch (Exception e) {
      try {
        return compareToUuid(o);
      } catch (Exception ex) {
        return compareToString(o);
      }
    }
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

  @Override
  public String toString() {
    return value;
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
}
