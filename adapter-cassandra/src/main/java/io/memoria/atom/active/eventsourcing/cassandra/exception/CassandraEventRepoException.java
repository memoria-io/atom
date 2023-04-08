package io.memoria.atom.active.eventsourcing.cassandra.exception;

import io.memoria.atom.core.eventsourcing.exception.ESException;
import io.memoria.atom.core.eventsourcing.pipeline.repo.ESRow;

public interface CassandraEventRepoException extends ESException {
  class FailedAppend extends IllegalArgumentException implements CassandraEventRepoException {
    private static final String msg = "Event with SeqId:%s append operation wasn't applied in "
                                      + "Keyspace:%s, Table:%s, StateId:%s";

    private FailedAppend(String keyspace, String table, String stateId, int seqId) {
      super(msg.formatted(seqId, keyspace, table, stateId));
    }

    public static FailedAppend of(String keyspace, String table, String stateId, int seqId) {
      return new FailedAppend(keyspace, table, stateId, seqId);
    }

    public static FailedAppend of(String keyspace, ESRow esRow) {
      return FailedAppend.of(keyspace, esRow.table(), esRow.stateId(), esRow.seqId());
    }
  }
}
