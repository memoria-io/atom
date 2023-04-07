package io.memoria.atom.active.eventsourcing.infra;

import io.memoria.atom.active.eventsourcing.infra.repo.ESRepo;
import io.memoria.atom.active.eventsourcing.infra.repo.EventRepo;
import io.memoria.atom.active.eventsourcing.infra.stream.ESStream;
import io.memoria.atom.active.eventsourcing.infra.stream.EventStream;
import io.memoria.atom.core.eventsourcing.Event;
import io.memoria.atom.core.eventsourcing.EventId;
import io.memoria.atom.core.eventsourcing.StateId;
import io.memoria.atom.core.eventsourcing.infra.EventSyncRoute;
import io.memoria.atom.core.eventsourcing.infra.Topic;
import io.memoria.atom.core.text.TextTransformer;
import io.vavr.control.Try;

import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Stream;

class RepoToStreamImpl<E extends Event> implements RepoToStream<E> {
  private final EventSyncRoute eventSyncRoute;
  private final EventRepo<E> eventRepo;
  private final EventStream<E> esStream;
  private final Set<EventId> ingested;
  private final AtomicInteger seqId;

  RepoToStreamImpl(EventSyncRoute eventSyncRoute,
                   ESRepo esRepo,
                   ESStream esStream,
                   TextTransformer transformer,
                   Class<E> eClass) {
    this.eventSyncRoute = eventSyncRoute;
    this.eventRepo = EventRepo.create(eventSyncRoute.eventTable(), esRepo, transformer, eClass);
    var qRoute = new Topic(eventSyncRoute.eventTopic(), eventSyncRoute.); this.esStream = EventStream.create(qRoute)
    this.ingested = ingested;
    this.seqId = seqId;
  }

  @Override
  public Stream<Try<E>> sync(StateId stateId) {
    esStream.sub(stateId).filter(this::skipDuplicates).peek(this::add)
    return eventRepo.getAll(stateId, seqId.get()).map(row -> esStream.pub(toESStreamMsg(row)));
  }

  private boolean skipDuplicates(Try<E> e) {
    if (e.isSuccess()) {
      return !ingested.contains(e.get().eventId());
    } else {
      return true; // to keep failures
    }
  }

  private void add(Try<E> e) {
    if (e.isSuccess()) {
      ingested.add(e.get().eventId());
      seqId.incrementAndGet();
    }
  }
}
