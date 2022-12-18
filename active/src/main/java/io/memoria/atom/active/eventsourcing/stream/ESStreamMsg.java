package io.memoria.atom.active.eventsourcing.stream;

public record ESStreamMsg(String topic, int partition, String key, String value) {}
