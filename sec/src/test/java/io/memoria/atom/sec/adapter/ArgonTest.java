package io.memoria.atom.sec.adapter;

import io.memoria.atom.core.sec.Hasher;
import io.memoria.atom.core.sec.Verifier;
import io.memoria.atom.core.text.TextGenerator;
import io.memoria.reactive.sec.argon.ArgonHasher;
import io.memoria.reactive.sec.argon.ArgonVerifier;
import io.vavr.collection.Stream;
import org.junit.jupiter.api.Test;

import java.security.SecureRandom;

class ArgonTest {
  private final Hasher hasher = new ArgonHasher(100, 1024, 4);
  private final Verifier verifier = new ArgonVerifier();

  @Test
  void hashAndVerifyTest() {
    SecureRandom secRand = new SecureRandom();
    var text = new TextGenerator(secRand);
    Stream.range(0, 10).forEach(t -> {
      int min = secRand.nextInt(20);
      int max = min + 200;
      String password = text.minMaxAlphanumeric(min, max);
      String salt = text.minMaxAlphanumeric(min, max);
      var m = hasher.hash(password, salt);
      assert verifier.verify(password, m, salt);
    });
  }
}
