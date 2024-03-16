package io.memoria.atom.eventsourcing;

public class ESException extends Exception {
  protected ESException(String msg) {
    super(msg);
  }
}
