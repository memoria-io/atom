package io.memoria.atom.core.eventsourcing.infra;

/**
 * "Q" as query in CQRS
 */
public record Topic(String topic, int nPartitions) {
  public String toShortString() {
    return "Route(%s,%d)".formatted(topic, nPartitions);
  }
}
