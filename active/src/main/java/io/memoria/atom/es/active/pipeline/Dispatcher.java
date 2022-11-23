package io.memoria.atom.es.active.pipeline;

import io.memoria.atom.core.eventsourcing.Command;
import io.memoria.atom.core.eventsourcing.Event;
import io.memoria.atom.core.eventsourcing.State;
import io.memoria.atom.core.eventsourcing.StateId;
import io.memoria.atom.es.active.repo.CommandRepo;
import io.memoria.atom.es.active.repo.EventRepo;
import io.vavr.control.Try;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Stream;

public class Dispatcher<S extends State, C extends Command, E extends Event> {
  private static final Logger log = LogManager.getLogger(Dispatcher.class.getSimpleName());

  private final Domain<S, C, E> domain;
  private final CommandRepo<C> commandRepo;
  private final EventRepo<E> EventRepo;
  private final Map<StateId, Pipeline<S, C, E>> pipelines;

  public Dispatcher(Domain<S, C, E> domain, CommandRepo<C> commandRepo, EventRepo<E> EventRepo) {
    this.domain = domain;
    this.commandRepo = commandRepo;
    this.EventRepo = EventRepo;
    this.pipelines = new ConcurrentHashMap<>();
  }

  public Stream<Try<Boolean>> dispatch() {
    return commandRepo.stream().map(cmdTry -> cmdTry.flatMap(this::handle));
  }

  private Try<Boolean> handle(C cmd) {
    pipelines.computeIfAbsent(cmd.stateId(), s -> {
      var pipeline = new StatePipeline<>(domain, commandRepo, EventRepo);
      Thread.startVirtualThread(() -> pipeline.stream().forEach(this::execute));
      return pipeline;
    });
    return pipelines.get(cmd.stateId()).offer(cmd);
  }

  private void execute(Try<S> tr) {
    if (tr.isSuccess()) {
      log.info(tr.get());
    } else {
      log.error("Pipeline Error:", tr.getCause());
    }
  }
}
