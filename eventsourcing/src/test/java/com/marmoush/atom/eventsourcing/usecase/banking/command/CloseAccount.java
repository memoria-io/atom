package com.marmoush.atom.eventsourcing.usecase.banking.command;

import com.marmoush.atom.eventsourcing.command.CommandMeta;

public record CloseAccount(CommandMeta meta) implements AccountCommand {}
