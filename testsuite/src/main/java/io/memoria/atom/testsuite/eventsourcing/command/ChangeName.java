package io.memoria.atom.testsuite.eventsourcing.command;

import io.memoria.atom.eventsourcing.command.CommandMeta;

public record ChangeName(CommandMeta meta, String name) implements AccountCommand {}
