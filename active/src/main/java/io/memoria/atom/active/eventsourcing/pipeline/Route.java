package io.memoria.atom.active.eventsourcing.pipeline;

public record Route(String cmdTopic, int cmdPartition, int totalCmdPartitions, String eventTopic) {}
