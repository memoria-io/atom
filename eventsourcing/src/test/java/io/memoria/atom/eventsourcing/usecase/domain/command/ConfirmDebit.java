package io.memoria.atom.eventsourcing.usecase.domain.command;

import io.memoria.atom.eventsourcing.command.CommandMeta;
import io.memoria.atom.eventsourcing.state.StateId;

public record ConfirmDebit(CommandMeta meta, StateId creditedAcc) implements AccountCommand {}
