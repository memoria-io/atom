package io.memoria.atom.jackson.adapters.id;

import io.memoria.atom.core.id.Id;

public class AnotherId extends Id {
  public AnotherId(String value) {
    super(value);
  }

  public AnotherId(Id id) {
    super(id);
  }

  public static AnotherId of(Id id) {
    return new AnotherId(id);
  }
}
