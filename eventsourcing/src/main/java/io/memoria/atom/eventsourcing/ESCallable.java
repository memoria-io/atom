package io.memoria.atom.eventsourcing;

import io.memoria.atom.core.domain.Shardable;

@FunctionalInterface
public interface ESCallable<V extends Shardable> {
  /**
   * Computes a result, or throws an exception if unable to do so.
   *
   * @return computed result
   *
   * @throws ESException if unable to compute a result
   */
  V call() throws ESException;
}