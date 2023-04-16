package io.memoria.atom.core.id.generator;

import io.memoria.atom.core.id.Id;

import java.util.concurrent.atomic.AtomicLong;
import java.util.function.Supplier;

@FunctionalInterface
public interface IdGenerator extends Supplier<Id> {
  @Override
  Id get();

  /**
   * <p>
   * Creates UUID v7 generator based on <a
   * href="https://github.com/uuid6/uuid6-ietf-draft">https://github.com/uuid6/uuid6-ietf-draft</a>
   * </p>
   *
   * <p>
   * Based on library <a href="https://github.com/f4b6a3/uuid-creator"> https://github.com/f4b6a3/uuid-creator </a>
   * </p>
   */
  static IdGenerator createTimeUUIDGenerator() {
    return new TimedUUIDGenerator();
  }

  static IdGenerator createSeqIdGenerator() {
    return new SeqIdGenerator();
  }

  static IdGenerator createSeqIdGenerator(long start) {
    return new SeqIdGenerator(new AtomicLong(start));
  }
}
