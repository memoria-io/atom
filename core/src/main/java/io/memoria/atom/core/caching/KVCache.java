package io.memoria.atom.core.caching;

import io.vavr.control.Option;

import java.util.LinkedHashMap;
import java.util.Map;

public class KVCache<K, V> {
  LinkedHashMap<K, V> cache;
  int capacity;

  public KVCache(int capacity) {
    cache = LinkedHashMap.newLinkedHashMap(capacity);
    this.capacity = capacity;
  }

  public Option<V> get(K key) {
    return Option.of(cache.get(key));
  }

  public void put(K key, V value) {
    if (cache.size() == capacity) {
      Map.Entry<K, V> entry = cache.entrySet().iterator().next();
      cache.remove(entry.getKey());
    }
    cache.put(key, value);
  }
}