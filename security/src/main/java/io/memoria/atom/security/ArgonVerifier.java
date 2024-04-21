package io.memoria.atom.security;

import de.mkammerer.argon2.Argon2;
import de.mkammerer.argon2.Argon2Factory;
import io.memoria.atom.core.security.Verifier;

public record ArgonVerifier(Argon2 argon2) implements Verifier {

  public ArgonVerifier() {
    this(Argon2Factory.create(Argon2Factory.Argon2Types.ARGON2id));
  }

  public boolean verify(String password, String hash, String salt) {
    boolean verify = argon2.verify(hash, (password + salt).getBytes());
    argon2.wipeArray(password.toCharArray());
    return verify;
  }
}
