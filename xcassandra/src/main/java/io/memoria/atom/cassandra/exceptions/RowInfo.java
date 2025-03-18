package io.memoria.atom.cassandra.exceptions;

import java.io.Serializable;

public record RowInfo(String keyspace, String table, String partitionKey, long clusterKey) implements Serializable {}
