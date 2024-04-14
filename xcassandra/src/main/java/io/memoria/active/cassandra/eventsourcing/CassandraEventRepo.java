package io.memoria.active.cassandra.eventsourcing;

import com.datastax.oss.driver.api.core.ConsistencyLevel;
import com.datastax.oss.driver.api.core.CqlSession;
import com.datastax.oss.driver.api.core.cql.Row;
import io.memoria.active.cassandra.exceptions.RowInfo;
import io.memoria.active.cassandra.exceptions.XCassandraRTEAppend;
import io.memoria.active.cassandra.exceptions.XCassandraRTETransform;
import io.memoria.atom.core.text.TextException;
import io.memoria.atom.core.text.TextTransformer;
import io.memoria.atom.eventsourcing.event.Event;
import io.memoria.atom.eventsourcing.event.repo.EventRepo;
import io.memoria.atom.eventsourcing.state.StateId;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

import static com.datastax.oss.driver.api.core.ConsistencyLevel.LOCAL_QUORUM;

public class CassandraEventRepo implements EventRepo {
  private final CqlSession session;
  private final ConsistencyLevel writeConsistency;
  private final ConsistencyLevel readConsistency;
  private final String keyspace;
  private final String table;
  private final TextTransformer transformer;

  public CassandraEventRepo(CqlSession session,
                            ConsistencyLevel writeConsistency,
                            ConsistencyLevel readConsistency,
                            String keyspace,
                            String table,
                            TextTransformer transformer) {
    this.session = session;
    this.writeConsistency = writeConsistency;
    this.readConsistency = readConsistency;
    this.keyspace = keyspace;
    this.table = table;
    this.transformer = transformer;
  }

  /**
   * Using LOCAL_QUORUM as default for read and write consistency
   */
  public CassandraEventRepo(CqlSession session, String keyspace, String table, TextTransformer transformer) {
    this(session, LOCAL_QUORUM, LOCAL_QUORUM, keyspace, table, transformer);
  }

  @Override
  public void append(Event event) {
    var payload = transformer.serialize(event);
    String partitionKey = event.pKey().value();
    long clusterKey = event.version();
    var st = EventTableStatements.push(keyspace, table, partitionKey, clusterKey, payload)
                                 .setConsistencyLevel(writeConsistency);
    var result = session.execute(st);
    if (!result.wasApplied()) {
      var rowInfo = new RowInfo(keyspace, table, partitionKey, clusterKey);
      throw new XCassandraRTEAppend(rowInfo);
    }
  }

  @Override
  public List<Event> fetch(StateId stateId) {
    String partitionKey = stateId.value();
    var st = EventTableStatements.fetchAll(keyspace, table, partitionKey, 0).setConsistencyLevel(readConsistency);
    List<Event> list = new ArrayList<>();

    for (Row row : session.execute(st).all()) {
      long clusterKey = row.getLong(EventTableStatements.CLUSTER_KEY_COL);
      var payload = Objects.requireNonNull(row.getString(EventTableStatements.PAYLOAD_COL));
      var rowInfo = new RowInfo(keyspace, table, partitionKey, clusterKey);
      list.add(toEvent(payload, rowInfo));
    }
    return list;
  }

  private Event toEvent(String payload, RowInfo rowInfo) {
    try {
      return transformer.deserialize(payload, Event.class);
    } catch (TextException e) {
      throw new XCassandraRTETransform(rowInfo, e);
    }
  }

  @Override
  public long size(StateId stateId) {
    var st = EventTableStatements.size(keyspace, table, stateId.value());
    return Optional.ofNullable(session.execute(st).one()).map(r -> r.getLong(0)).orElse(0L);
  }
}
