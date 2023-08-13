package io.memoria.atom.core.caching;

import io.vavr.collection.Stream;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;

class MemKCacheTest {
  private final int range = 1000_000;
  private final int capacity = 100;
  private final MemKCache<Integer> cache = new MemKCache<>(capacity);

  @Test
  void check() {
    Stream.range(0, range).forEach(i -> {
      cache.add(i);
      Assertions.assertThat(cache.hashSet).hasSizeLessThanOrEqualTo(capacity);
    });
  }
}
