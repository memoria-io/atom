package com.marmoush.atom.eventsourcing.usecase.simple;

import com.marmoush.atom.eventsourcing.command.Command;
import com.marmoush.atom.eventsourcing.command.CommandMeta;

public record ChangeState(CommandMeta meta) implements Command {}
