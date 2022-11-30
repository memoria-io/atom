package io.memoria.atom.active.eventsourcing.cassandra;

import com.datastax.oss.driver.api.core.CqlSession;
import com.datastax.oss.driver.api.core.cql.Row;
import com.datastax.oss.driver.api.core.cql.SimpleStatement;
import io.memoria.atom.active.eventsourcing.cassandra.exception.CassandraEventRepoException.FailedAppend;
import io.memoria.atom.active.eventsourcing.infra.event.EventMsg;
import io.memoria.atom.active.eventsourcing.infra.event.EventMsgRepo;
import io.memoria.atom.core.eventsourcing.StateId;
import io.vavr.control.Try;

import java.util.stream.Stream;
import java.util.stream.StreamSupport;

/**
 * EventRepo's secondary/driven adapter of cassandra
 */
public class CassandraEventMsgRepo implements EventMsgRepo {
  private final String keyspace;
  private final CqlSession session;

  public CassandraEventMsgRepo(String keyspace, CqlSession session) {
    this.keyspace = keyspace;
    this.session = session;
  }

  @Override
  public Stream<EventMsg> getAll(String topic, StateId stateId) {
    return get(keyspace, topic, stateId.value()).map(row -> toEventMsg(topic, row));
  }

  @Override
  public Try<Integer> append(EventMsg eventMsg) {
    return push(keyspace, eventMsg.topic(), eventMsg.stateId().value(), eventMsg.seqId(), eventMsg.value());
  }

  private EventMsg toEventMsg(String topic, EventRow row) {
    return EventMsg.create(topic, StateId.of(row.stateId()), row.seqId(), row.event());
  }

  private Stream<EventRow> get(String keyspace, String table, String stateId) {
    var st = Statements.get(keyspace, table, stateId);
    return execSelect(session, st).map(EventRow::from);
  }

  private Try<Integer> push(String keyspace, String table, String stateId, int seqId, String event) {
    var lastRowSt = Statements.getLastSeqId(keyspace, table, stateId);
    var firstOpt = execSelect(session, lastRowSt).findFirst();
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
    var st = Statements.push(keyspace, table, eventRow);
    if (session.execute(st).wasApplied()) {
      return Try.success(seqId);
    } else {
      return Try.failure(FailedAppend.of(keyspace, table, stateId, seqId));
    }
  }

  public static Stream<Row> execSelect(CqlSession session, SimpleStatement st) {
    var rs = session.execute(st);
    return StreamSupport.stream(rs.spliterator(), false);
  }
}
