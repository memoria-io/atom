package io.memoria.atom.core.caching;

import java.util.LinkedHashSet;

public class KCache<K> {
  LinkedHashSet<K> cache;
  int capacity;

  public KCache(int capacity) {
    cache = LinkedHashSet.newLinkedHashSet(capacity);
    this.capacity = capacity;
  }

  public boolean contains(K key) {
    return cache.contains(key);
  }

  public void add(K key) {
    if (cache.size() == capacity) {
      cache.remove(cache.iterator().next());
    }
    cache.add(key);
  }
}