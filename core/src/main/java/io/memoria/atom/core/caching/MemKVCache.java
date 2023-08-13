package io.memoria.atom.core.caching;

import io.vavr.control.Option;

import java.util.LinkedHashMap;
import java.util.Map;

class MemKVCache<K, V> implements KVCache<K, V> {
  LinkedHashMap<K, V> hashMap;
  int capacity;

  public MemKVCache(int capacity) {
    hashMap = LinkedHashMap.newLinkedHashMap(capacity);
    this.capacity = capacity;
  }

  @Override
  public Option<V> get(K key) {
    return Option.of(hashMap.get(key));
  }

  @Override
  public void put(K key, V value) {
    if (hashMap.size() == capacity) {
      Map.Entry<K, V> entry = hashMap.entrySet().iterator().next();
      hashMap.remove(entry.getKey());
    }
    hashMap.put(key, value);
  }
}