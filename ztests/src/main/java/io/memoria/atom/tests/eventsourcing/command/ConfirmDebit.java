package io.memoria.atom.tests.eventsourcing.command;

import io.memoria.atom.eventsourcing.command.CommandMeta;
import io.memoria.atom.eventsourcing.state.StateId;

public record ConfirmDebit(CommandMeta meta, StateId creditedAcc) implements AccountCommand {}
