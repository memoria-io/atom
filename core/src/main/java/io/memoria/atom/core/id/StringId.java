package io.memoria.atom.core.id;

public record StringId(String value) implements IdValue {
  public StringId {
    if (value == null || value.isEmpty())
      throw new IllegalArgumentException("Id value is null or empty.");
  }

  @Override
  public int compareTo(IdValue o) {
    return value.compareTo(o.value());
  }
}
