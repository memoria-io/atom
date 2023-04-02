package io.memoria.atom.active.eventsourcing.cassandra;

import com.datastax.oss.driver.api.core.CqlSession;
import com.datastax.oss.driver.api.core.cql.Row;
import com.datastax.oss.driver.api.core.cql.SimpleStatement;
import io.memoria.atom.active.eventsourcing.cassandra.exception.CassandraEventRepoException.FailedAppend;
import io.memoria.atom.active.eventsourcing.infra.repo.ESRepo;
import io.memoria.atom.core.eventsourcing.infra.repo.ESRepoRow;
import io.vavr.control.Try;

import java.util.stream.Stream;
import java.util.stream.StreamSupport;

/**
 * EventRepo's secondary/driven adapter of cassandra
 */
public class CassandraESRepo implements ESRepo {
  private final String keyspace;
  private final CqlSession session;

  public CassandraESRepo(String keyspace, CqlSession session) {
    this.keyspace = keyspace;
    this.session = session;
  }

  @Override
  public Stream<ESRepoRow> getFirst(String table, String stateId) {
    return getFirst(keyspace, table, stateId).map(cassandraRow -> toESRepoRow(table, cassandraRow));
  }

  @Override
  public Stream<ESRepoRow> getAll(String table, String stateId) {
    return get(keyspace, table, stateId, 0).map(cassandraRow -> toESRepoRow(table, cassandraRow));
  }

  @Override
  public Stream<ESRepoRow> getAll(String table, String stateId, int startIdx) {
    return get(keyspace, table, stateId, startIdx).map(cassandraRow -> toESRepoRow(table, cassandraRow));
  }

  @Override
  public Try<ESRepoRow> append(ESRepoRow r) {
    return push(keyspace, r.table(), r.stateId(), r.seqId(), r.value()).map(i -> r);
  }

  private ESRepoRow toESRepoRow(String table, CassandraRow r) {
    return new ESRepoRow(table, r.stateId(), r.seqId(), r.payload());
  }

  private Stream<CassandraRow> get(String keyspace, String table, String stateId, int seqId) {
    var st = Statements.get(keyspace, table, stateId, seqId);
    return execSelect(session, st).map(CassandraRow::from);
  }

  private Stream<CassandraRow> getFirst(String keyspace, String table, String stateId) {
    var st = Statements.getFirst(keyspace, table, stateId);
    return execSelect(session, st).map(CassandraRow::from);
  }

  private Try<Integer> push(String keyspace, String table, String stateId, int seqId, String event) {
    var lastRowSt = Statements.getLastSeqId(keyspace, table, stateId);
    var firstOpt = execSelect(session, lastRowSt).findFirst();
    if (firstOpt.isPresent()) {
      var lastSeqId = CassandraRow.from(firstOpt.get()).seqId();
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
    var eventRow = new CassandraRow(stateId, seqId, event);
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
