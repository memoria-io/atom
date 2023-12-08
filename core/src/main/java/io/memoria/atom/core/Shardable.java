package io.memoria.atom.core;

import io.memoria.atom.core.id.Id;

public interface Shardable {

  Id shardKey();

  default boolean isInPartition(int partition, int totalPartitions) {
    return partition == partition(totalPartitions);
  }

  default int partition(int totalPartitions) {
    return partition(shardKey(), totalPartitions);
  }

  static int partition(Id shardKey, int totalPartitions) {
    var hash = (shardKey.hashCode() == Integer.MIN_VALUE) ? Integer.MAX_VALUE : shardKey.hashCode();
    return Math.abs(hash) % totalPartitions;
  }
}
