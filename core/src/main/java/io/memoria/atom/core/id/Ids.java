package io.memoria.atom.core.id;

import java.util.UUID;

public class Ids {
  private Ids() {}

  public static Id of(String value) {
    return new Id(value);
  }

  public static Id of(long value) {
    return new Id(value);
  }

  public static Id of(UUID uuid) {
    return new Id(uuid);
  }
}
