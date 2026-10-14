package com.marmoush.atom.eventsourcing.usecase.banking.command;

import com.marmoush.atom.eventsourcing.command.CommandMeta;
import com.marmoush.atom.eventsourcing.state.StateId;

public record ConfirmDebit(CommandMeta meta, StateId creditedAcc) implements AccountCommand {}
