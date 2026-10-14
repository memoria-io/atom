package com.marmoush.atom.eventsourcing.usecase.banking.command;

import com.marmoush.atom.eventsourcing.command.CommandMeta;

public record CreateAccount(CommandMeta meta, String accountName, long balance) implements AccountCommand {}
