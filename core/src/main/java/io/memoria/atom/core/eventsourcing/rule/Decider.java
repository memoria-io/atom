package io.memoria.atom.core.eventsourcing.rule;

import io.memoria.atom.core.eventsourcing.Command;
import io.memoria.atom.core.eventsourcing.Event;
import io.memoria.atom.core.eventsourcing.State;
import io.vavr.Function2;
import io.vavr.control.Try;

@FunctionalInterface
public interface Decider<S extends State, C extends Command, E extends Event> extends Function2<S, C, Try<E>> {

}
