package io.memoria.atom.core.caching;

import io.vavr.control.Option;

public interface KVCache<K, V> {
  Option<V> get(K key);

  void put(K key, V value);

  /**
   * @param capacity is the max size of the queue
   * @return in memory fifo cache
   */
  static <K, V> KVCache<K, V> inMemory(int capacity) {
    return new MemKVCache<>(capacity);
  }
}
