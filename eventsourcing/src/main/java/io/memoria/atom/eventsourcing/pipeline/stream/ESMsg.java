package io.memoria.atom.eventsourcing.pipeline.stream;

public record ESMsg(String topic, int partition, String key, String value) {}
