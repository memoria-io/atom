package io.memoria.atom.eventsourcing.data;

import io.memoria.atom.eventsourcing.state.State;
import io.memoria.atom.eventsourcing.state.StateMeta;

public record SomeState(StateMeta meta) implements State {}
