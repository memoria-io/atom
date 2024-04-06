package io.memoria.atom.core.text;

public class TextException extends Exception {
  protected TextException(Exception e) {
    super(e);
  }

  protected TextException(String message) {
    super(message);
  }

  public static TextException of(Exception e) {
    return new TextException(e);
  }

  public static TextException of(String message) {
    return new TextException(message);
  }
}
