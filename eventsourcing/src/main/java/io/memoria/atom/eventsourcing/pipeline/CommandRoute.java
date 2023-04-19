package io.memoria.atom.eventsourcing.pipeline;

public record CommandRoute(String cmdTopic, String eventTopic, int topicPartition, int totalPartitions) {
  public CommandRoute {
    if (cmdTopic == null || cmdTopic.isEmpty() || eventTopic == null || eventTopic.isEmpty()) {
      throw new IllegalArgumentException("Topic name can't be null or empty string");
    }
    if (topicPartition < 0) {
      throw new IllegalArgumentException("Partition can't be less than 0");
    }
    if (totalPartitions < 1) {
      throw new IllegalArgumentException("Replicas must be from 1 to 5 inclusive.");
    }
  }

  public String toShortString() {
    return "Route(%s_%d -> %s_%d)".formatted(cmdTopic, topicPartition, eventTopic, topicPartition);
  }
}
