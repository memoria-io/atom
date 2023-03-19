package io.memoria.atom.core.eventsourcing;

public record Route(String cmdTopic, int cmdPartition, int totalCmdPartitions, String eventTable) {
  public String toShortString() {
    return "Route(%s,%d,%s)".formatted(cmdTopic, cmdPartition, eventTable);
  }
}
