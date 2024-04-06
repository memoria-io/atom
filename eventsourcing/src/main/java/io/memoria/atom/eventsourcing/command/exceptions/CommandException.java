package io.memoria.atom.eventsourcing.command.exceptions;

import io.memoria.atom.eventsourcing.ESException;
import io.memoria.atom.eventsourcing.command.Command;

public interface CommandException extends ESException {
  Command command();
}
