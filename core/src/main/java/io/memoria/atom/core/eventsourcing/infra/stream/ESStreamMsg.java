package io.memoria.atom.core.eventsourcing.infra.stream;

public record ESStreamMsg(String topic, int partition, String key, String value) {}
