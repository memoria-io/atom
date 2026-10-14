package com.marmoush.javakit.eventsourcing.usecase.simple;

import com.marmoush.javakit.eventsourcing.command.Command;
import com.marmoush.javakit.eventsourcing.command.CommandMeta;

public record ChangeState(CommandMeta meta) implements Command {}
