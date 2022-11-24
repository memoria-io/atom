package io.memoria.atom.active.eventsourcing.cassandra;

import com.datastax.oss.driver.api.core.CqlSession;
import io.memoria.atom.active.eventsourcing.repo.EventMsg;
import io.memoria.atom.active.eventsourcing.repo.EventRepo;
import io.memoria.atom.core.eventsourcing.StateId;
import io.vavr.control.Try;

import java.util.stream.Stream;

public class CassandraEventRepo implements EventRepo {
  private final String keyspace;
  private final QueryClient client;

  public CassandraEventRepo(String keyspace, CqlSession cqlSession) {
    this.keyspace = keyspace;
    this.client = new QueryClient(cqlSession);
  }

  @Override
  public Stream<EventMsg> getAll(String topic, StateId stateId) {
    return client.get(keyspace, topic, stateId.value()).map(row -> toEventMsg(topic, row));
  }

  @Override
  public Try<Integer> append(EventMsg eventMsg) {
    return client.push(keyspace, eventMsg.topic(), eventMsg.stateId().value(), eventMsg.seqId(), eventMsg.value());

  }

  private EventMsg toEventMsg(String topic, EventRow row) {
    return EventMsg.create(topic, StateId.of(row.stateId()), row.seqId(), row.event());
  }
}
