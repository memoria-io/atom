package io.memoria.atom.actor;

public class ActorException extends Exception {
  protected ActorException(Exception e) {
    super(e);
  }

  public static ActorException of(Exception e) {
    return new ActorException(e);
  }
}
