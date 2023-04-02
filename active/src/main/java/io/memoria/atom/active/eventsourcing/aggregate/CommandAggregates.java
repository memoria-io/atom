package io.memoria.atom.active.eventsourcing.aggregate;

import io.memoria.atom.active.eventsourcing.infra.repo.ESRepo;
import io.memoria.atom.active.eventsourcing.infra.stream.CommandStream;
import io.memoria.atom.active.eventsourcing.infra.stream.ESStream;
import io.memoria.atom.core.eventsourcing.*;
import io.memoria.atom.core.eventsourcing.infra.CRoute;
import io.memoria.atom.core.text.TextTransformer;
import io.vavr.control.Try;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ThreadFactory;
import java.util.function.Consumer;
import java.util.stream.Stream;

public class CommandAggregates<S extends State, C extends Command, E extends Event> implements AutoCloseable {
  private static final Logger log = LoggerFactory.getLogger(CommandAggregates.class.getSimpleName());

  private final Domain<S, C, E> domain;
  private final CRoute CRoute;
  private final ESStream esStream;
  private final ESRepo esRepo;
  private final TextTransformer transformer;
  private final Consumer<Try<E>> eventConsumer;
  private final ThreadFactory factory;
  private final Map<StateId, CommandAggregateTask<S, C, E>> tasks;

  public CommandAggregates(Domain<S, C, E> domain,
                           CRoute CRoute,
                           ESStream esStream,
                           ESRepo esRepo,
                           TextTransformer transformer,
                           Consumer<Try<E>> eventConsumer) {
    this.domain = domain;
    this.CRoute = CRoute;
    this.esStream = esStream;
    this.esRepo = esRepo;
    this.transformer = transformer;
    this.eventConsumer = eventConsumer;
    this.factory = Thread.ofVirtual().name(getThreadPrefix(), 0).factory();
    this.tasks = new ConcurrentHashMap<>();
  }

  public Stream<Try<C>> run() {
    var commandStream = CommandStream.create(CRoute, esStream, transformer, domain.cClass());
    return commandStream.sub().map(cTry -> cTry.flatMap(this::append));
  }

  private Try<C> append(C cmd) {
    tasks.computeIfAbsent(cmd.stateId(), s -> {
      var aggregate = new CommandAggregate<>(domain, CRoute, esStream, esRepo, transformer);
      var agFu = new CommandAggregateTask<>(cmd.stateId(), aggregate, eventConsumer, factory);
      agFu.start();
      return agFu;
    });
    var appendResult = tasks.get(cmd.stateId()).append(cmd);
    if (appendResult.isFailure()) {
      var removedAgg = tasks.remove(cmd.stateId());
      if (removedAgg != null) {
        log.error("Aggregate %s was removed".formatted(removedAgg.name()), appendResult.getCause());
        removedAgg.interrupt();
      }
    }
    return appendResult;
  }

  @Override
  public void close() {
    this.tasks.values().forEach(CommandAggregateTask::interrupt);
  }

  private String getThreadPrefix() {
    return domain.toShortString() + "-" + CRoute.toShortString() + "_";
  }
}
