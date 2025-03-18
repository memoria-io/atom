package io.memoria.atom.cassandra.exceptions;

import java.util.Optional;

class XCassandraRTE extends RuntimeException {
  private final RowInfo rowInfo;

  XCassandraRTE(String message) {
    super(message);
    this.rowInfo = null;
  }

  XCassandraRTE(RowInfo rowInfo, String message) {
    super(message);
    this.rowInfo = rowInfo;
  }

  XCassandraRTE(RowInfo rowInfo, String message, Throwable cause) {
    super(message, cause);
    this.rowInfo = rowInfo;
  }

  public Optional<RowInfo> getRowInfo() {
    return Optional.ofNullable(rowInfo);
  }
}
