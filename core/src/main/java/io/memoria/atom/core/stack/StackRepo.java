package io.memoria.atom.core.stack;

import io.vavr.collection.List;
import io.vavr.control.Try;

public interface StackRepo {
  Try<StackItem> append(StackItem row);

  Try<List<StackItem>> fetch(StackId stackId);

  Try<Integer> size(StackId stackId);

  static StackRepo inMemory() {
    return new MemStackAdapter();
  }
}
