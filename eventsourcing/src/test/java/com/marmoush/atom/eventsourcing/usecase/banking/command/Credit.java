package com.marmoush.atom.eventsourcing.usecase.banking.command;

import com.marmoush.atom.eventsourcing.command.CommandMeta;
import com.marmoush.atom.eventsourcing.state.StateId;

public record Credit(CommandMeta meta, StateId debitedAcc, long amount) implements AccountCommand {}
