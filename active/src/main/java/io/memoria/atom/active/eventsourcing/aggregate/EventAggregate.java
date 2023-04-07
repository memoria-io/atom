package io.memoria.atom.active.eventsourcing.aggregate;

import io.memoria.atom.active.eventsourcing.infra.repo.ESRepo;
import io.memoria.atom.active.eventsourcing.infra.stream.EventStream;
import io.memoria.atom.core.eventsourcing.*;
import io.memoria.atom.core.eventsourcing.infra.CRoute;
import io.memoria.atom.core.eventsourcing.infra.Topic;
import io.memoria.atom.core.text.TextTransformer;
import io.vavr.control.Try;

import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Stream;

/**
 * Creates active stateful aggregation
 */
public class EventAggregate<S extends State, C extends Command, E extends Event> {
  // Core
  public final StateId stateId;
  public final Domain<S, C, E> domain;
  public final Topic topic;
  // Infra
  private final EventStream<E> eventStream;
  // In memory
  private final AtomicInteger eventSeqId;
  private S state;

  public EventAggregate(StateId stateId, Domain<S, C, E> domain, CRoute qRoute, ESRepo esRepo, TextTransformer transformer) {
    // Core
    this.stateId = stateId;
    this.domain = domain;
    this.topic = qRoute;
    // Infra
    this.eventStream = EventStream.create(qRoute, esRepo, transformer, domain.eClass());
    // In memory
    this.eventSeqId = new AtomicInteger();
    this.state = null;
  }

  Stream<Try<S>> stream() {
    return eventStream.getAll(stateId).map(eTry -> eTry.map(this::evolve));
  }

  S evolve(E e) {
    if (this.state == null) {
      this.state = domain.evolver().apply(e);
    } else {
      this.state = domain.evolver().apply(this.state, e);
    }
    this.eventSeqId.incrementAndGet();
    return this.state;
  }
}
