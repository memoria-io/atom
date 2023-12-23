package io.memoria.atom.core.id;

import java.io.Serializable;

public class Id implements Serializable, Comparable<Id> {
  private final IdValue idValue;

  public Id(IdValue idValue) {
    this.idValue = idValue;
  }

  public String value() {
    return this.idValue.value();
  }

  @Override
  public int compareTo(Id o) {
    return this.idValue.compareTo(o.idValue);
  }
}
