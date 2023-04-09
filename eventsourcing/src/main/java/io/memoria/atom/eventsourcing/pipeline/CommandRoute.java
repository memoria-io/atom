package io.memoria.atom.eventsourcing.pipeline;

public record CommandRoute(String eventTable,
                           String eventTopic,
                           int eventTopicPartition,
                           int eventTopicPartitions,
                           String cmdTopic,
                           int cmdTopicPartition,
                           int cmdTopicPartitions) {
  public String toShortString() {
    return "Route(eventTable[%s],eventTopic[%s:%d],commandTopic[%s,%d])".formatted(eventTable,
                                                                                   eventTopic,
                                                                                   eventTopicPartition,
                                                                                   cmdTopic,
                                                                                   cmdTopicPartition);
  }
}
