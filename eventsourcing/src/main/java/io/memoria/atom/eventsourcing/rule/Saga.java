package io.memoria.atom.eventsourcing.rule;

import io.memoria.active.eventsourcing.Command;
import io.memoria.active.eventsourcing.Event;
import io.vavr.Function1;
import io.vavr.control.Option;

@FunctionalInterface
public interface Saga<E extends Event, C extends Command> extends Function1<E, Option<C>> {}
