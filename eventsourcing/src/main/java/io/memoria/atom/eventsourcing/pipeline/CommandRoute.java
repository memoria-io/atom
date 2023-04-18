package io.memoria.atom.eventsourcing.pipeline;

public record CommandRoute(String cmdTopic,
                           int cmdTopicPartition,
                           int cmdTotalPartitions,
                           String eventTopic,
                           int eventTopicPartition,
                           int eventTotalPartitions) {
  public CommandRoute {
    if (cmdTopic == null || cmdTopic.isEmpty() || eventTopic == null || eventTopic.isEmpty()) {
      throw new IllegalArgumentException("Topic name can't be null or empty string");
    }
    if (cmdTopicPartition < 0 || eventTopicPartition < 0) {
      throw new IllegalArgumentException("Partition can't be less than 0");
    }
    if (cmdTotalPartitions < 1 || eventTotalPartitions < 1) {
      throw new IllegalArgumentException("Replicas must be from 1 to 5 inclusive.");
    }
  }

  public String toShortString() {
    return "Route(eventTopic[%s:%d],commandTopic[%s,%d])".formatted(eventTopic,
                                                                    eventTopicPartition,
                                                                    cmdTopic,
                                                                    cmdTopicPartition);
  }
}
