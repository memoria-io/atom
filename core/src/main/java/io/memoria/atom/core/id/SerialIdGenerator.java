package io.memoria.atom.core.id;

import java.util.concurrent.atomic.AtomicLong;

public record SerialIdGenerator(AtomicLong atomicLong) implements IdGenerator {
  public SerialIdGenerator() {
    this(new AtomicLong(0));
  }

  @Override
  public Id get() {
    return Id.of(atomicLong.getAndIncrement() + "");
  }
}
