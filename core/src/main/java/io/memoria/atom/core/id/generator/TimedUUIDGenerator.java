package io.memoria.atom.core.id.generator;

import io.memoria.atom.core.id.Id;

record TimedUUIDGenerator() implements IdGenerator {
  @Override
  public Id get() {
    return Id.of();
  }
}
