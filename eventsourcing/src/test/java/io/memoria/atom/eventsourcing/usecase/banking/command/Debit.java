package io.memoria.atom.eventsourcing.usecase.banking.command;

import io.memoria.atom.eventsourcing.command.CommandMeta;
import io.memoria.atom.eventsourcing.state.StateId;

public record Debit(CommandMeta meta, StateId creditedAcc, long amount) implements AccountCommand {}
