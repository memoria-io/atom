package io.memoria.atom.active.eventsourcing.infra;

import io.memoria.atom.active.eventsourcing.infra.repo.EventRepo;
import io.memoria.atom.active.eventsourcing.infra.stream.EventStream;
import io.memoria.atom.core.eventsourcing.Event;
import io.memoria.atom.core.eventsourcing.StateId;
import io.memoria.atom.core.eventsourcing.infra.EventSyncRoute;
import io.vavr.control.Try;

import java.util.stream.Stream;

public interface RepoToStream<E extends Event> {
  Stream<Try<E>> sync(StateId stateId);

  static <E extends Event> RepoToStream create(EventSyncRoute route, EventRepo<E> eventRepo, EventStream<E> eventStream) {
    return new RepoToStreamImpl(route, eventRepo, eventStream);
  }
}
