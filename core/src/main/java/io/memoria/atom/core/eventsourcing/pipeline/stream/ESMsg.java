package io.memoria.atom.core.eventsourcing.pipeline.stream;

public record ESMsg(String topic, int partition, String key, String value) {}
