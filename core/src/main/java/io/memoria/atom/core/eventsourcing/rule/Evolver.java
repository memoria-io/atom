package io.memoria.atom.core.eventsourcing.rule;

import io.memoria.atom.core.eventsourcing.Event;
import io.memoria.atom.core.eventsourcing.State;
import io.vavr.Function2;

@FunctionalInterface
public interface Evolver<S extends State, E extends Event> extends Function2<S, E, S> {

}
