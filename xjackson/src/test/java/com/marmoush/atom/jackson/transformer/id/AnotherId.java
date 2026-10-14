package com.marmoush.atom.jackson.transformer.id;

import com.marmoush.atom.core.id.Id;

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
