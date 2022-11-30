package io.memoria.atom.active.eventsourcing.cassandra;

import com.datastax.oss.driver.api.core.CqlSession;
import com.datastax.oss.driver.api.core.cql.Row;
import com.datastax.oss.driver.api.core.cql.SimpleStatement;
import io.memoria.atom.active.eventsourcing.cassandra.exception.CassandraEventRepoException.FailedAppend;
import io.memoria.atom.active.eventsourcing.pipeline.EventRepo;
import io.memoria.atom.core.eventsourcing.Event;
import io.memoria.atom.core.eventsourcing.StateId;
import io.memoria.atom.core.text.TextTransformer;
import io.vavr.control.Try;

import java.util.stream.Stream;
import java.util.stream.StreamSupport;

/**
 * EventRepo's secondary/driven adapter of cassandra
 */
public class CassandraEventRepo<E extends Event> implements EventRepo<E> {
  private final String keyspace;
  private final CqlSession session;
  private final TextTransformer transformer;
  private final Class<E> eClass;

  public CassandraEventRepo(String keyspace, CqlSession session, TextTransformer transformer, Class<E> eClass) {
    this.keyspace = keyspace;
    this.session = session;
    this.transformer = transformer;
    this.eClass = eClass;
  }

  @Override
  public Stream<Try<E>> getAll(String topic, StateId stateId) {
    return get(keyspace, topic, stateId.value()).map(row -> transformer.deserialize(row.event(), eClass));
  }

  @Override
  public Try<Integer> append(String topic, int seqId, E e) {
    return transformer.serialize(e).flatMap(v -> push(keyspace, topic, e.stateId().value(), seqId, v));
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
