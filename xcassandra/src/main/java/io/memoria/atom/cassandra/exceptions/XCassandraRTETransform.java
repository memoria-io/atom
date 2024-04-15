package io.memoria.atom.cassandra.exceptions;

public class XCassandraRTETransform extends XCassandraRTE {
  private static final String MSG = "Stack item with clustering key:%s ser/des operation wasn't applied in "
                                    + "Keyspace:%s, Table:%s, Partition:%s";

  public XCassandraRTETransform(RowInfo rowInfo, Throwable cause) {
    super(rowInfo, message(rowInfo), cause);
  }

  private static String message(RowInfo rowInfo) {
    return MSG.formatted(rowInfo.clusterKey(), rowInfo.keyspace(), rowInfo.table(), rowInfo.partitionKey());
  }
}
