package io.memoria.atom.testsuite.eventsourcing.banking.command;

import io.memoria.atom.eventsourcing.CommandMeta;
import io.memoria.atom.eventsourcing.StateId;

public record Credit(CommandMeta meta, StateId debitedAcc, long amount) implements AccountCommand {}
