package io.memoria.atom.active.cassandra;

public record ClientConfig(String datacenter, String ip, int port) {}
