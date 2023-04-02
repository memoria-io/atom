package io.memoria.atom.active.eventsourcing.infra.repo;

import io.memoria.atom.core.eventsourcing.infra.CRoute;
import io.memoria.atom.core.eventsourcing.infra.repo.ESRepoRow;
import io.memoria.atom.core.eventsourcing.Event;
import io.memoria.atom.core.eventsourcing.StateId;
import io.memoria.atom.core.text.TextTransformer;
import io.vavr.control.Try;

import java.util.stream.Stream;

class EventRepoImpl<E extends Event> implements EventRepo<E> {
  private final CRoute CRoute;
  private final ESRepo esRepo;
  private final TextTransformer transformer;
  private final Class<E> eClass;

  protected EventRepoImpl(CRoute CRoute, ESRepo esRepo, TextTransformer transformer, Class<E> eClass) {
    this.CRoute = CRoute;
    this.esRepo = esRepo;
    this.transformer = transformer;
    this.eClass = eClass;
  }

  @Override
  public Stream<Try<E>> getFirst(StateId stateId) {
    return esRepo.getFirst(CRoute.eventTable(), stateId.value()).map(this::deserialize);
  }

  @Override
  public Stream<Try<E>> getAll(StateId stateId) {
    return esRepo.getAll(CRoute.eventTable(), stateId.value()).map(this::deserialize);
  }

  @Override
  public Stream<Try<E>> getAll(StateId stateId, int seqId) {
    return esRepo.getAll(CRoute.eventTable(), stateId.value(), seqId).map(this::deserialize);
  }

  @Override
  public Try<Integer> append(int seqId, E event) {
    return transformer.serialize(event)
                      .map(eStr -> createRow(seqId, event.stateId().value(), eStr))
                      .flatMap(esRepo::append)
                      .map(ESRepoRow::seqId);
  }

  private ESRepoRow createRow(int seqId, String stateId, String eStr) {
    return new ESRepoRow(CRoute.eventTable(), stateId, seqId, eStr);
  }

  private Try<E> deserialize(ESRepoRow esRepoRow) {
    return transformer.deserialize(esRepoRow.value(), eClass);
  }
}
