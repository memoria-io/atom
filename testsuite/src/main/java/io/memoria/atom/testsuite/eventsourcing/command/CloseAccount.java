package io.memoria.atom.testsuite.eventsourcing.command;

import io.memoria.atom.eventsourcing.CommandMeta;

public record CloseAccount(CommandMeta meta) implements AccountCommand {}
