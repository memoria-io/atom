package io.memoria.atom.es.active.cassandra.client;

import com.datastax.oss.driver.api.core.CqlSession;
import io.memoria.atom.core.eventsourcing.StateId;
import io.memoria.atom.es.active.cassandra.infra.ExecUtils;

import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Stream;

public class QueryClient {
  private final AtomicInteger idx = new AtomicInteger(-1);
  private final CqlSession session;

  public QueryClient(ClientConfig config) {
    this.session = SessionUtils.session(config).build();
  }

  public Stream<EventRow> get(String keyspace, String table, StateId stateId) {
    var st = EventRowSts.get(keyspace, table, stateId);
    return ExecUtils.execSelect(session, st).map(EventRow::from);
  }

  public boolean push(String keyspace, String table, StateId stateId, String event) {
    var lastRowSt = EventRowSts.getLastSeqId(keyspace, table, stateId);
    if (idx.get() < 0) {
      var firstOpt = ExecUtils.execSelect(session, lastRowSt).findFirst();
      if (firstOpt.isPresent()) {
        var lastSeqId = EventRow.from(firstOpt.get()).seqId();
        idx.set(lastSeqId);
      } else {
        idx.set(0);
      }
    }
    var eventRow = new EventRow(stateId, idx.getAndIncrement(), event);
    var st = EventRowSts.push(keyspace, table, eventRow);
    return ExecUtils.exec(session, st);
  }
}
