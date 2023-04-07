package io.memoria.atom.core.eventsourcing.infra;

/**
 * "Q" as query in CQRS
 */
public record EventSyncRoute(String eventTable, String eventTopic, int eventTopicTotalPartitions) {
  public String toShortString() {
    return "Route(%s,%s,%d)".formatted(eventTable, eventTopic, eventTopicTotalPartitions);
  }
}
