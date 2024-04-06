package io.memoria.atom.actor;

public class ActorException extends Exception {

  protected ActorException(Exception e) {
    super(e);
  }

  protected ActorException(String message){
    super(message);
  }

  public static ActorException of(Exception e) {
    return new ActorException(e);
  }

  public static ActorException of(String message) {
    return new ActorException(message);
  }
}
