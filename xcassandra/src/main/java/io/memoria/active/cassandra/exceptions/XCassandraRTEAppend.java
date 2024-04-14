package io.memoria.active.cassandra.exceptions;

public class XCassandraRTEAppend extends XCassandraRTE {
  private static final String MSG = "Stack item with clustering key:%s append operation wasn't applied in "
                                    + "Keyspace:%s, Table:%s, Partition:%s";

  public XCassandraRTEAppend(RowInfo rowInfo) {
    super(rowInfo, message(rowInfo));
  }

  private static String message(RowInfo rowInfo) {
    return MSG.formatted(rowInfo.clusterKey(), rowInfo.keyspace(), rowInfo.table(), rowInfo.partitionKey());
  }
}