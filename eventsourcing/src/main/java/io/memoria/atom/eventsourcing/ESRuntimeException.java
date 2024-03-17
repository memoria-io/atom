package io.memoria.atom.eventsourcing;

public class ESRuntimeException extends RuntimeException {
  protected ESRuntimeException(String msg) {
    super(msg);
  }
}
