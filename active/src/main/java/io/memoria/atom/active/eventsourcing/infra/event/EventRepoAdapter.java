package io.memoria.atom.active.eventsourcing.infra.event;

import io.memoria.atom.active.eventsourcing.pipeline.EventRepo;
import io.memoria.atom.core.eventsourcing.Event;
import io.memoria.atom.core.eventsourcing.StateId;
import io.memoria.atom.core.text.TextTransformer;
import io.vavr.control.Try;

import java.util.stream.Stream;

public class EventRepoAdapter<E extends Event> implements EventRepo<E> {
  private final EventMsgRepo repo;
  private final TextTransformer transformer;
  private final Class<E> eClass;

  public EventRepoAdapter(EventMsgRepo repo, TextTransformer transformer, Class<E> eClass) {
    this.repo = repo;
    this.transformer = transformer;
    this.eClass = eClass;
  }

  public Stream<Try<E>> getAll(String topic, StateId stateId) {
    return repo.getAll(topic, stateId).map(eMsg -> transformer.deserialize(eMsg.value(), eClass));
  }

  public Try<Integer> append(String topic, int seqId, E e) {
    return transformer.serialize(e).map(v -> EventMsg.create(topic, e.stateId(), seqId, v)).flatMap(repo::append);
  }
}
