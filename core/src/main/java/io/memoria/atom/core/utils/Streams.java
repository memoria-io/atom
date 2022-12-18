package io.memoria.atom.core.utils;

import io.vavr.collection.List;

import java.util.concurrent.BlockingDeque;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.stream.Stream;

public class Streams {
  private Streams() {}

  public static <T> Stream<T> merge(List<Stream<T>> streams) {
    BlockingDeque<T> deque = new LinkedBlockingDeque<>();
    for (Stream<T> stream : streams) {
      Thread.startVirtualThread(() -> stream.forEachOrdered(s -> {
        try {
          deque.putLast(s);
        } catch (InterruptedException e) {
          throw new RuntimeException(e);
        }
      }));
    }
    return Stream.generate(() -> {
      try {
        return deque.take();
      } catch (InterruptedException e) {
        throw new RuntimeException(e);
      }
    });
  }
}
