package io.memoria.atom.eventsourcing.rule;

import io.memoria.atom.eventsourcing.command.Command;
import io.memoria.atom.eventsourcing.command.CommandMeta;

public record CreateState(CommandMeta meta) implements Command {}
