package com.marmoush.javakit.eventsourcing.usecase.banking.command;

import com.marmoush.javakit.eventsourcing.command.CommandMeta;

public record CreateAccount(CommandMeta meta, String accountName, long balance) implements AccountCommand {}
