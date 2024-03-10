package io.memoria.atom.eventsourcing;

public class ESException extends Throwable {
  protected ESException(String msg) {
    super(msg);
  }
}
