package io.memoria.atom.core.eventsourcing.pipeline.repo;

import io.memoria.atom.core.eventsourcing.Event;
import io.memoria.atom.core.eventsourcing.StateId;
import io.memoria.atom.core.text.TextTransformer;
import io.vavr.control.Try;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import static io.memoria.atom.core.vavr.ReactorVavrUtils.toMono;

class EventRepoImpl<E extends Event> implements EventRepo<E> {
  private final String eventTable;
  private final ESRowRepo esRowRepo;
  private final TextTransformer transformer;
  private final Class<E> eClass;

  protected EventRepoImpl(String eventTable, ESRowRepo esRowRepo, TextTransformer transformer, Class<E> eClass) {
    this.eventTable = eventTable;
    this.esRowRepo = esRowRepo;
    this.transformer = transformer;
    this.eClass = eClass;
  }

  @Override
  public Flux<E> getAll(StateId stateId) {
    return esRowRepo.getAll(eventTable, stateId.value()).flatMap(this::deserialize);
  }

  @Override
  public Flux<E> append(Flux<E> events) {
    var flux = events.concatMap(e -> toMono(toESRow(e)));
    return esRowRepo.append(eventTable, flux).flatMap(esRow -> toMono(transformer.deserialize(esRow.value(), eClass)));
  }

  private Try<ESRow> toESRow(E e) {
    return transformer.serialize(e).map(eventStr -> new ESRow(eventTable, e.stateId().value(), e.seqId(), eventStr));
  }

  private Mono<E> deserialize(ESRow esRow) {
    return toMono(transformer.deserialize(esRow.value(), eClass));
  }
}
