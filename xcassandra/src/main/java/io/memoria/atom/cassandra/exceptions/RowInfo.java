package io.memoria.atom.cassandra.exceptions;

public record RowInfo(String keyspace, String table, String partitionKey, long clusterKey) {}
