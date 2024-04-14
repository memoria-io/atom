package io.memoria.atom.web.security;

import io.memoria.atom.core.security.Hasher;
import io.memoria.atom.core.security.Verifier;
import io.memoria.atom.core.text.TextGenerator;
import org.junit.jupiter.api.Test;

import java.security.SecureRandom;
import java.util.stream.IntStream;

class ArgonTest {
  private final Hasher hasher = new ArgonHasher(100, 1024, 4);
  private final Verifier verifier = new ArgonVerifier();

  @Test
  void hashAndVerifyTest() {
    SecureRandom secRand = new SecureRandom();
    var text = new TextGenerator(secRand);
    IntStream.range(0, 10).forEach(t -> {
      int min = secRand.nextInt(20);
      int max = min + 200;
      String password = text.minMaxAlphanumeric(min, max);
      String salt = text.minMaxAlphanumeric(min, max);
      var m = hasher.hash(password, salt);
      assert verifier.verify(password, m, salt);
    });
  }
}
