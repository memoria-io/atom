package io.memoria.atom.core.eventsourcing.pipeline;

import io.memoria.atom.core.eventsourcing.Event;
import io.memoria.atom.core.eventsourcing.EventId;
import io.memoria.atom.core.eventsourcing.pipeline.repo.ESRowRepo;
import io.memoria.atom.core.eventsourcing.pipeline.repo.EventRepo;
import io.memoria.atom.core.eventsourcing.pipeline.stream.ESMsgStream;
import io.memoria.atom.core.eventsourcing.pipeline.stream.EventStream;
import io.memoria.atom.core.text.TextTransformer;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.HashSet;
import java.util.Set;

class StreamToRepoImpl<E extends Event> implements StreamToRepo<E> {
  private final EventRepo<E> eventRepo;
  private final EventStream<E> eventStream;
  private final Set<EventId> alreadyPersisted;

  StreamToRepoImpl(String tableName,
                   String topicName,
                   int partition,
                   ESMsgStream esMsgStream,
                   ESRowRepo esRowRepo,
                   TextTransformer transformer,
                   Class<E> eClass) {
    this.eventRepo = EventRepo.create(tableName, esRowRepo, transformer, eClass);
    this.eventStream = EventStream.create(topicName, partition, esMsgStream, transformer, eClass);
    this.alreadyPersisted = new HashSet<>();
  }

  @Override
  public Flux<E> sync() {
    return eventStream.sub().flatMap(this::loadEvents).flatMap(e -> eventRepo.append(e).map(i -> e));
  }

  private Mono<E> loadEvents(E e) {
    if (alreadyPersisted.contains(e.eventId())) {
      return Mono.empty();
    } else {
      return eventRepo.getAll(e.stateId()).map(event -> this.alreadyPersisted.add(event.eventId())).then(Mono.just(e));
    }
  }
}
