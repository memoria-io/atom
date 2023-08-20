package io.memoria.atom.core.caching;

import io.vavr.control.Option;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.ReentrantLock;

public abstract class KVCache<K, V> {
  private final Map<K, ReentrantLock> lockMap = new ConcurrentHashMap<>();

  abstract Option<V> get(K key);

  abstract void put(K key, V value);

  public V putIfAbsent(K key, V value) {
    lockMap.computeIfAbsent(key, k -> new ReentrantLock());
    lockMap.get(key).lock();
    put(key, value);
    lockMap.get(key).unlock();
    return value;
  }

  /**
   * @param capacity is the max size of the queue
   * @return in memory fifo cache
   */
  static <K, V> KVCache<K, V> inMemory(int capacity) {
    return new MemKVCache<>(capacity);
  }
}
