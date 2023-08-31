package io.memoria.atom.core.caching;

public interface KCache<K> {
  boolean contains(K key);

  void add(K key);

  /**
   * @param capacity is the max size of the queue
   * @return in memory fifo cache
   */
  static <K> KCache<K> inMemory(int capacity) {
    return new MemKCache<>(capacity);
  }
}
