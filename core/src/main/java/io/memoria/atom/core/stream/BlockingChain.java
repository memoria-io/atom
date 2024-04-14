package io.memoria.atom.core.stream;

import java.util.concurrent.Callable;
import java.util.stream.Stream;

public interface BlockingChain<T> {
  void append(T t);

  Stream<Callable<T>> fetch();

  static <T> BlockingChain<T> inMemory() {
    return new MemBlockingChain<>();
  }
}
