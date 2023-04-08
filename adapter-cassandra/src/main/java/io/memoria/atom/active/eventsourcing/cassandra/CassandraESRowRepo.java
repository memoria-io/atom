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
  public Flux<ESRow> append(String table, Flux<ESRow> rows) {
    return rows.concatMap(r -> appendESRow(keyspace, r));
  }

  Flux<CassandraRow> get(String keyspace, String table, String stateId) {
    var st = Statements.get(keyspace, table, stateId, 0);
    return execSelect(st).map(CassandraRow::from);
  }

  Mono<ESRow> appendESRow(String keyspace, ESRow esRow) {
    var row = new CassandraRow(esRow.stateId(), esRow.seqId(), esRow.value());
    return Mono.from(session.executeReactive(Statements.push(keyspace, esRow.table(), row)))
               .map(ReactiveRow::wasApplied)
               .flatMap(applied -> applied ? Mono.just(esRow) : Mono.error(FailedAppend.of(keyspace, esRow)));
  }

  private ESRow toESRepoRow(String table, CassandraRow r) {
    return new ESRow(table, r.stateId(), r.seqId(), r.payload());
  }

  public Flux<ReactiveRow> execSelect(SimpleStatement st) {
    return Flux.from(session.executeReactive(st));
  }
}
