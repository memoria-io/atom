package io.memoria.atom.core.eventsourcing.infra;

/**
 * "Q" as query in CQRS
 */
public record QRoute(String eventTopic, int eventTopicPartition, int eventTopicTotalPartitions) {
  public String toShortString() {
    return "Route(%s,%d,%s)".formatted(eventTopic, eventTopicPartition, eventTopicTotalPartitions);
  }
}
