package io.memoria.atom.cassandra.exceptions;

public class XCassandraRTEAppendFailure extends XCassandraRTE {
  private static final String MSG = "Stack item with clusteringKey:%s append operation wasn't applied in "
                                    + "Keyspace:%s, Table:%s, partitionKey:%s";

  public XCassandraRTEAppendFailure(RowInfo rowInfo) {
    super(rowInfo, message(rowInfo));
  }

  private static String message(RowInfo rowInfo) {
    return MSG.formatted(rowInfo.clusterKey(), rowInfo.keyspace(), rowInfo.table(), rowInfo.partitionKey());
  }
}