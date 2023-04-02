package io.memoria.atom.core.eventsourcing.infra;

/**
 * "C" as command in CQRS
 */
public record CRoute(String cmdTopic,
                     int cmdTopicSrcPartition,
                     int cmdTopicTotalPartitions,
                     String eventTable) {
  public String toShortString() {
    return "Route(%s,%d,%s)".formatted(cmdTopic, cmdTopicSrcPartition, eventTable);
  }
}
