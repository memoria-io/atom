package io.memoria.atom.core.eventsourcing.rule;

import io.memoria.atom.core.eventsourcing.Event;
import io.memoria.atom.core.eventsourcing.State;
import io.vavr.Function1;

@FunctionalInterface
public interface Reducer<S extends State, E extends Event> extends Function1<S, E> {}
