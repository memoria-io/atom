package io.memoria.atom.eventsourcing.exceptions;

public class ESException extends Throwable {
  protected ESException(String msg) {
    super(msg);
  }
}
