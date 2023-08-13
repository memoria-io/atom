package io.memoria.atom.core.caching;

import java.util.LinkedHashSet;

class MemKCache<K> implements KCache<K> {
  LinkedHashSet<K> hashSet;
  int capacity;

  public MemKCache(int capacity) {
    hashSet = LinkedHashSet.newLinkedHashSet(capacity);
    this.capacity = capacity;
  }

  @Override
  public boolean contains(K key) {
    return hashSet.contains(key);
  }

  @Override
  public void add(K key) {
    if (hashSet.size() == capacity) {
      hashSet.remove(hashSet.iterator().next());
    }
    hashSet.add(key);
  }
}