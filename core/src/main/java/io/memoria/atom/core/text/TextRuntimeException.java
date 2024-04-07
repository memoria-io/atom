package io.memoria.atom.core.text;

public class TextRuntimeException extends RuntimeException {

  protected TextRuntimeException(Exception e) {
    super(e);
  }

  protected TextRuntimeException(String message) {
    super(message);
  }

  public static TextRuntimeException of(Exception e) {
    return new TextRuntimeException(e);
  }

  public static TextRuntimeException of(String message) {
    return new TextRuntimeException(message);
  }
}
