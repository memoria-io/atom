package io.memoria.atom.core.caching;

import io.vavr.collection.Stream;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;

class KVCacheTest {
  private final int range = 1000_000;
  private final int capacity = 100;
  private final KVCache<Integer, Integer> cache = new KVCache<>(capacity);

  @Test
  void check() {
    Stream.range(0, range).forEach(i -> {
      cache.put(i, i);
      Assertions.assertThat(cache.cache).hasSizeLessThanOrEqualTo(capacity);
    });
  }
}
