package io.memoria.atom.active.eventsourcing.cassandra;

public record ClientConfig(String datacenter, String ip, int port) {}
