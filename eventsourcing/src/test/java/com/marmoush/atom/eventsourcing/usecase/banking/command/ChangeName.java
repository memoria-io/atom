package com.marmoush.atom.eventsourcing.usecase.banking.command;

import com.marmoush.atom.eventsourcing.command.CommandMeta;

public record ChangeName(CommandMeta meta, String name) implements AccountCommand {}
