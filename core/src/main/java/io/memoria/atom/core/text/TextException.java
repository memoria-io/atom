package io.memoria.atom.core.text;

public class TextException extends Exception {
  protected TextException(Exception exception) {
    super(exception);
  }

  protected TextException(String message) {
    super(message);
  }

  public static TextException of(Exception exception) {
    return new TextException(exception);
  }

  public static TextException of(String message) {
    return new TextException(message);
  }
}
