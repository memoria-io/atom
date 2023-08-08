package io.memoria.atom.testsuite.eventsourcing.banking.command;

import io.memoria.atom.eventsourcing.CommandMeta;

public record CloseAccount(CommandMeta meta, long timestamp) implements AccountCommand {}
