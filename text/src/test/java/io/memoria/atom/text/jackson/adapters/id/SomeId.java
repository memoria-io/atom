package io.memoria.atom.text.jackson.adapters.id;

import io.memoria.atom.core.id.Id;

public record SomeId(String value) implements Id {
  @Override
  public int compareTo(Id o) {
    return value.compareTo(o.value());
  }
}