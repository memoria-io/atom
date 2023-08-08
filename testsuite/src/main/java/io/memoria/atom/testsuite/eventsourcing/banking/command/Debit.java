package io.memoria.atom.testsuite.eventsourcing.banking.command;

import io.memoria.atom.eventsourcing.CommandMeta;
import io.memoria.atom.eventsourcing.StateId;

public record Debit(CommandMeta meta, StateId creditedAcc, long amount) implements AccountCommand {}
