package io.memoria.reactive.eventsourcing.pipeline;

import io.memoria.atom.core.eventsourcing.Command;
import io.memoria.atom.core.eventsourcing.Event;
import io.memoria.atom.core.eventsourcing.rule.Saga;

public record SagaDomain<E extends Event, C extends Command>(Class<E> eventClass,
                                                             Class<C> commandClass,
                                                             Saga<E, C> decider) {}
