package io.memoria.atom.web;

import io.memoria.atom.web.auth.BasicCredential;
import io.memoria.atom.web.auth.Credential;
import io.memoria.atom.web.auth.Token;

import java.util.Base64;
import java.util.NoSuchElementException;

public final class HttpUtils {

  private HttpUtils() {}

  public static Credential credential(String header) {
    var trimmedHeader = header.trim();
    if (trimmedHeader.contains("Basic")) {
      String content = trimmedHeader.split(" ")[1].trim();
      String[] basic = new String(Base64.getDecoder().decode(content)).split(":");
      return new BasicCredential(basic[0], basic[1]);
    }
    if (trimmedHeader.contains("Bearer")) {
      return new Token(header.split(" ")[1].trim());
    } else {
      throw new NoSuchElementException();
    }
  }
}
