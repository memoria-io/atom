package io.memoria.atom.active.eventsourcing.repo;

import io.memoria.atom.core.eventsourcing.Event;
import io.memoria.atom.core.eventsourcing.StateId;
import io.memoria.atom.core.text.TextTransformer;
import io.vavr.control.Try;

import java.util.stream.Stream;

class AbstractEventRepo<E extends Event> implements EventRepo<E> {
  private final ESRepo esRepo;
  private final TextTransformer transformer;
  private final Class<E> eClass;

  protected AbstractEventRepo(ESRepo esRepo, TextTransformer transformer, Class<E> eClass) {
    this.esRepo = esRepo;
    this.transformer = transformer;
    this.eClass = eClass;
  }

  @Override
  public Stream<Try<E>> getAll(String topic, StateId stateId) {
    return esRepo.getAll(topic, stateId.value()).map(this::deserialize);
  }

  @Override
  public Stream<Try<E>> getAll(String topic, StateId stateId, int seqId) {
    return esRepo.getAll(topic, stateId.value(), seqId).map(this::deserialize);
  }

  @Override
  public Try<Integer> append(String topic, int seqId, E event) {
    return transformer.serialize(event)
                      .flatMap(eStr -> esRepo.append(new ESRepoRow(topic, event.stateId().value(), seqId, eStr)))
                      .map(ESRepoRow::seqId);
  }

  private Try<E> deserialize(ESRepoRow esRepoRow) {
    return transformer.deserialize(esRepoRow.value(), eClass);
  }
}
