package io.memoria.atom.active.eventsourcing.cassandra;

import com.datastax.oss.driver.api.core.CqlSession;
import io.memoria.atom.active.eventsourcing.cassandra.exception.CassandraEventRepoException.FailedAppend;
import io.memoria.atom.active.eventsourcing.cassandra.infra.ExecUtils;
import io.vavr.control.Try;

import java.util.stream.Stream;

class QueryClient {
  private final CqlSession session;

  QueryClient(CqlSession session) {
    this.session = session;
  }

  public Stream<EventRow> get(String keyspace, String table, String stateId) {
    var st = EventRowSts.get(keyspace, table, stateId);
    return ExecUtils.execSelect(session, st).map(EventRow::from);
  }

  public Try<Integer> push(String keyspace, String table, String stateId, int seqId, String event) {
    var lastRowSt = EventRowSts.getLastSeqId(keyspace, table, stateId);
    var firstOpt = ExecUtils.execSelect(session, lastRowSt).findFirst();
    if (firstOpt.isPresent()) {
      var lastSeqId = EventRow.from(firstOpt.get()).seqId();
      if (seqId == lastSeqId + 1) {
        return pushMsg(keyspace, table, stateId, seqId, event);
      } else {
        return Try.failure(FailedAppend.of(keyspace, table, stateId, seqId));
      }
    } else {
      return pushMsg(keyspace, table, stateId, seqId, event);
    }
  }

  private Try<Integer> pushMsg(String keyspace, String table, String stateId, int seqId, String event) {
    var eventRow = new EventRow(stateId, seqId, event);
    var st = EventRowSts.push(keyspace, table, eventRow);
    var wasApplied = ExecUtils.exec(session, st);
    if (wasApplied) {
      return Try.success(seqId);
    } else {
      return Try.failure(FailedAppend.of(keyspace, table, stateId, seqId));
    }
  }
}
