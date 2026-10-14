package com.marmoush.javakit.core.domain;

import com.marmoush.javakit.core.id.Id;

public interface Partitioned {

  /**
   * @return the partitioning Key
   */
  Id pKey();

  default boolean isInPartition(int partition, int totalPartitions) {
    return partition == partition(totalPartitions);
  }

  default int partition(int totalPartitions) {
    return partition(pKey(), totalPartitions);
  }

  static int partition(Id pKey, int totalPartitions) {
    var hash = (pKey.hashCode() == Integer.MIN_VALUE) ? Integer.MAX_VALUE : pKey.hashCode();
    return Math.abs(hash) % totalPartitions;
  }
}
