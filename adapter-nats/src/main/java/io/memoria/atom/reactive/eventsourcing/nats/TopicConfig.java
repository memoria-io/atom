package io.memoria.atom.reactive.eventsourcing.nats;

import io.nats.client.api.StorageType;

import java.time.Duration;

/**
 * @param topic
 * @param storageType
 * @param replicationFactor
 * @param maxBatchSize          for each pull from the server
 * @param maxWaitBeforeRetry
 * @param minRetriesOfFetchLast after fetched messages has zero elements
 * @param maxRetriesOfFetchLast the max amount of retries to fetch last element
 * @param denyDelete
 * @param denyPurge
 */
public record TopicConfig(Topic topic,
                          StorageType storageType,
                          int replicationFactor,
                          int maxBatchSize,
                          Duration maxWaitBeforeRetry,
                          int minRetriesOfFetchLast,
                          int maxRetriesOfFetchLast,
                          boolean denyDelete,
                          boolean denyPurge) {

  public TopicConfig {
    if (minRetriesOfFetchLast > maxRetriesOfFetchLast) {
      throw new IllegalArgumentException("Minimum retries can't be more than max retries");
    }
  }

  static TopicConfig appendOnly(Topic topic,
                                StorageType storageType,
                                int replicationFactor,
                                int maxBatchSize,
                                Duration maxWaitBeforeRetry,
                                int minRetriesOfFetchLast,
                                int maxRetriesOfFetchLast) {
    return new TopicConfig(topic,
                           storageType,
                           replicationFactor,
                           maxBatchSize,
                           maxWaitBeforeRetry,
                           minRetriesOfFetchLast,
                           maxRetriesOfFetchLast,
                           true,
                           true);
  }
}
