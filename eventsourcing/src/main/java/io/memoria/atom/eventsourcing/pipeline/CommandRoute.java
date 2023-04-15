package io.memoria.atom.eventsourcing.pipeline;

public record CommandRoute(String eventTopic,
                           int eventTopicPartition,
                           int eventTotalPartitions,
                           String cmdTopic,
                           int cmdTopicPartition,
                           int cmdTotalPartitions) {
  public String toShortString() {
    return "Route(eventTopic[%s:%d],commandTopic[%s,%d])".formatted(eventTopic,
                                                                    eventTopicPartition,
                                                                    cmdTopic,
                                                                    cmdTopicPartition);
  }
}
