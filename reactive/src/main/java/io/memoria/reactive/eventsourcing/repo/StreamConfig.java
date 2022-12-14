package io.memoria.reactive.eventsourcing.repo;

public record StreamConfig(String name, int totalPartitions, int history) {
  public StreamConfig(String name, int totalPartitions) {
    this(name, totalPartitions, Integer.MAX_VALUE);
  }

  public StreamConfig withHistory(int newHistory) {
    return new StreamConfig(name, totalPartitions, newHistory);
  }
}
