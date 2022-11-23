package io.memoria.atom.es.active.cassandra;

import io.memoria.atom.core.eventsourcing.Event;
import io.memoria.atom.core.eventsourcing.StateId;
import io.memoria.atom.core.text.TextTransformer;
import io.memoria.atom.es.active.cassandra.client.EventRow;
import io.memoria.atom.es.active.cassandra.client.QueryClient;
import io.memoria.atom.es.active.cassandra.exception.CassandraEventRepoException.FailedPush;
import io.memoria.atom.es.active.repo.EventRepo;
import io.vavr.control.Try;

import java.util.stream.Stream;

public class CassandraEventRepo<E extends Event> implements EventRepo<E> {
  private final String keyspace;
  private final String topic;
  private final Class<E> eClass;
  private final TextTransformer transformer;
  private final QueryClient client;

  public CassandraEventRepo(String keyspace,
                            String topic,
                            Class<E> eClass,
                            TextTransformer transformer,
                            QueryClient client) {
    this.keyspace = keyspace;
    this.topic = topic;
    this.eClass = eClass;
    this.transformer = transformer;
    this.client = client;
  }

  @Override
  public Stream<Try<E>> get(StateId stateId) {
    return client.get(keyspace, topic, stateId).map(this::toEvent);
  }

  @Override
  public Try<E> push(E event) {
    return transformer.serialize(event)
                      .map(str -> client.push(keyspace, topic, event.stateId(), str))
                      .flatMap(b -> handle(event, b));
  }

  private Try<E> handle(E event, Boolean b) {
    if (b) {
      return Try.success(event);
    } else {
      return Try.failure(FailedPush.of(keyspace, topic, event.stateId(), event.eventId()));
    }
  }

  private Try<E> toEvent(EventRow row) {
    return transformer.deserialize(row.event(), eClass);
  }
}
