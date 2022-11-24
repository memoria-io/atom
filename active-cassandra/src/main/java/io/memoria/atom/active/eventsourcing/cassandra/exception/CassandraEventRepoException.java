package io.memoria.atom.active.eventsourcing.cassandra.exception;

import io.memoria.atom.core.eventsourcing.EventId;
import io.memoria.atom.core.eventsourcing.StateId;
import io.memoria.atom.core.eventsourcing.exception.ESException;

public interface CassandraEventRepoException extends ESException {
  class FailedPush extends IllegalArgumentException implements CassandraEventRepoException {
    private static final String msg = "Event with EventId:%s push operation wasn't applied in "
                                      + "Keyspace:%s, Table:%s, StateId:%s";

    private FailedPush(String keyspace, String table, StateId stateId, EventId eventId) {
      super(msg.formatted(eventId.value(), keyspace, table, stateId));
    }

    public static FailedPush of(String keyspace, String table, StateId stateId, EventId eventId) {
      return new FailedPush(keyspace, table, stateId, eventId);
    }
  }
}
