package com.marmoush.atom.eventsourcing.usecase.banking.command;

import com.marmoush.atom.eventsourcing.command.CommandMeta;
import com.marmoush.atom.eventsourcing.state.StateId;

public record Debit(CommandMeta meta, StateId creditedAcc, long amount) implements AccountCommand {}
