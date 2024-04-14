package io.memoria.active.cassandra.exceptions;

public record RowInfo(String keyspace, String table, String partitionKey, long clusterKey) {}
