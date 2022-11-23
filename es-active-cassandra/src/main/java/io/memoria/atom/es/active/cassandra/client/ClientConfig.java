package io.memoria.atom.es.active.cassandra.client;

public record ClientConfig(String datacenter, String ip, int port) {}
