package io.memoria.atom.core.id.generator;

import io.memoria.atom.core.id.Id;

import java.util.concurrent.atomic.AtomicLong;

record SeqIdGenerator(AtomicLong atomicLong) implements IdGenerator {
  public SeqIdGenerator() {
    this(new AtomicLong(0));
  }

  @Override
  public Id get() {
    return Id.of(atomicLong.getAndIncrement());
  }
}
