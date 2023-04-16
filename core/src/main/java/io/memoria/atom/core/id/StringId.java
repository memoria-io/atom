package io.memoria.atom.core.id;

record StringId(String value) implements Id {
  public StringId {
    if (value == null || value.isEmpty())
      throw new IllegalArgumentException("Id value is null or empty.");
  }

  @Override
  public int compareTo(Id o) {
    return value.compareTo(o.value());
  }
}
