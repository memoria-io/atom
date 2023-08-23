package io.memoria.atom.core.caching;

import io.vavr.control.Option;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.ReentrantLock;
import java.util.function.Function;

public abstract class KVCache<K, V> {
  protected final Map<K, ReentrantLock> lockMap = new ConcurrentHashMap<>();

  public abstract Option<V> get(K key);

  public abstract void put(K key, V value);

  public void putIfAbsent(K key, Function<K,V> fn) {
    lockMap.computeIfAbsent(key, k -> new ReentrantLock());
    lockMap.get(key).lock();
    if (get(key).isEmpty()) {
      put(key, fn.apply(key));
    }
    lockMap.get(key).unlock();
  }

  /**
   * @param capacity is the max size of the queue
   * @return in memory fifo cache
   */
  public static <K, V> KVCache<K, V> inMemory(int capacity) {
    return new MemKVCache<>(capacity);
  }
}
