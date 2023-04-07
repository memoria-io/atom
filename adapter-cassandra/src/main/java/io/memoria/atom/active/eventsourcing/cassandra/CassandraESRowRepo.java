package io.memoria.atom.active.eventsourcing.cassandra;

import com.datastax.dse.driver.api.core.cql.reactive.ReactiveRow;
import com.datastax.oss.driver.api.core.CqlSession;
import com.datastax.oss.driver.api.core.cql.SimpleStatement;
import io.memoria.atom.active.eventsourcing.cassandra.exception.CassandraEventRepoException.FailedAppend;
import io.memoria.atom.core.eventsourcing.pipeline.repo.ESRow;
import io.memoria.atom.core.eventsourcing.pipeline.repo.ESRowRepo;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * EventRepo's secondary/driven adapter of cassandra
 */
public class CassandraESRowRepo implements ESRowRepo {
  private final String keyspace;
  private final CqlSession session;

  public CassandraESRowRepo(String keyspace, CqlSession session) {
    this.keyspace = keyspace;
    this.session = session;
  }

  @Override
  public Flux<ESRow> getAll(String table, String stateId) {
    return get(keyspace, table, stateId).map(cassandraRow -> toESRepoRow(table, cassandraRow));
  }

  @Override
  public Mono<ESRow> append(String table, String stateId, String value) {
    var lastSeqStatement = Statements.getLastSeqId(keyspace, table, stateId);
    return execSelect(lastSeqStatement).map(CassandraRow::from)
                                       .map(CassandraRow::seqId)
                                       .single(0)
                                       .flatMap(seqId -> exec(keyspace, new ESRow(table, stateId, seqId, value)));
  }

  private ESRow toESRepoRow(String table, CassandraRow r) {
    return new ESRow(table, r.stateId(), r.seqId(), r.payload());
  }

  private Flux<CassandraRow> get(String keyspace, String table, String stateId) {
    var st = Statements.get(keyspace, table, stateId, 0);
    return execSelect(st).map(CassandraRow::from);
  }

  private Mono<ESRow> exec(String keyspace, ESRow esRow) {
    var row = new CassandraRow(esRow.stateId(), esRow.seqId(), esRow.value());
    var st = Statements.push(keyspace, esRow.table(), row);
    return Mono.from(session.executeReactive(st).wasApplied())
               .flatMap(applied -> applied ? Mono.just(esRow) : Mono.error(failedAppend(keyspace, esRow)));
  }

  private static FailedAppend failedAppend(String keyspace, ESRow esRow) {
    return FailedAppend.of(keyspace, esRow.table(), esRow.stateId(), esRow.seqId());
  }

  public Flux<ReactiveRow> execSelect(SimpleStatement st) {
    return Flux.from(session.executeReactive(st));
  }
}
