package io.memoria.reactive.eventsourcing.pipeline;

import io.memoria.atom.core.eventsourcing.Command;
import io.memoria.atom.core.eventsourcing.Event;
import io.memoria.atom.core.eventsourcing.State;
import io.memoria.atom.core.eventsourcing.rule.Decider;
import io.memoria.atom.core.eventsourcing.rule.Evolver;
import io.memoria.atom.core.eventsourcing.rule.Reducer;

public record Domain<S extends State, C extends Command, E extends Event>(Class<S> stateClass,
                                                                          Class<C> commandClass,
                                                                          Class<E> eventClass,
                                                                          S initState,
                                                                          Decider<S, C, E> decider,
                                                                          Evolver<S, E> evolver,
                                                                          Reducer<S, E> reducer) {}
