package io.memoria.atom.jackson.transformer.id;

import io.memoria.atom.core.id.Id;

public class SomeId extends Id {
  public SomeId(String value) {
    super(value);
  }

  public SomeId(Id id) {
    super(id);
  }

  public static SomeId of(Id id) {
    return new SomeId(id);
  }
}
