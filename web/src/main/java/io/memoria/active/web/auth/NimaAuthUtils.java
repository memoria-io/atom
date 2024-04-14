package io.memoria.active.web.auth;

import io.helidon.http.HeaderNames;
import io.helidon.webserver.http.ServerRequest;
import io.memoria.active.web.HttpUtils;

import java.util.NoSuchElementException;
import java.util.Optional;

public class NimaAuthUtils {

  private NimaAuthUtils() {}

  public static Optional<Credential> credential(ServerRequest req) {
    try {
      var value = req.headers().get(HeaderNames.AUTHORIZATION).get();
      return Optional.of(HttpUtils.credential(value));
    } catch (NoSuchElementException e) {
      return Optional.empty();
    }
  }
}