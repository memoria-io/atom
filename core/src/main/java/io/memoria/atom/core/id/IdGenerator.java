package io.memoria.atom.core.id;

import java.util.function.Supplier;

@FunctionalInterface
public interface IdGenerator extends Supplier<Id> {
  @Override
  Id get();
}
