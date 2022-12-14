package io.memoria.atom.active.eventsourcing.pipeline;

import io.memoria.atom.active.eventsourcing.repo.EventRepo;
import io.memoria.atom.active.eventsourcing.stream.CommandStream;
import io.memoria.atom.core.eventsourcing.Command;
import io.memoria.atom.core.eventsourcing.Event;
import io.memoria.atom.core.eventsourcing.State;
import io.vavr.control.Try;

import java.util.function.Consumer;
import java.util.stream.Stream;

interface Pipeline<C extends Command, E extends Event> {
  Try<Void> append(C cmd);

  Stream<Try<E>> stream();

  static <S extends State, C extends Command, E extends Event> Pipeline<C, E> create(Domain<S, C, E> domain,
                                                                                     Route route,
                                                                                     CommandStream<C> commandStream,
                                                                                     EventRepo<E> eventRepo,
                                                                                     Consumer<Try<E>> resultConsumer) {
    return new StatePipeline<>(domain, route, commandStream, eventRepo, resultConsumer);
  }
}
