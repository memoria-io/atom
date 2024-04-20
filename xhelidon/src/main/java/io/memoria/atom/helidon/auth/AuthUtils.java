package io.memoria.atom.helidon.auth;

import io.helidon.http.HeaderNames;
import io.helidon.webserver.http.ServerRequest;
import io.memoria.atom.web.HttpUtils;
import io.memoria.atom.web.auth.Credential;

import java.util.NoSuchElementException;
import java.util.Optional;

public class AuthUtils {

  private AuthUtils() {}

  public static Optional<Credential> credential(ServerRequest req) {
    try {
      var value = req.headers().get(HeaderNames.AUTHORIZATION).get();
      return Optional.of(HttpUtils.credential(value));
    } catch (NoSuchElementException e) {
      return Optional.empty();
    }
  }
}