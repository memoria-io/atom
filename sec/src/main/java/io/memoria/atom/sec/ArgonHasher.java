package io.memoria.atom.sec;

import de.mkammerer.argon2.Argon2;
import de.mkammerer.argon2.Argon2Factory;
import io.memoria.atom.core.sec.Hasher;

public record ArgonHasher(Argon2 argon2, int iterations, int memory, int parallelism) implements Hasher {

  public ArgonHasher(int iterations, int memory, int parallelism) {
    this(Argon2Factory.create(Argon2Factory.Argon2Types.ARGON2id), iterations, memory, parallelism);
  }

  @Override
  public String hash(String password, String salt) {
    String saltedPass = password + salt;
    String hash = argon2.hash(iterations, memory, parallelism, saltedPass.getBytes());
    argon2.wipeArray(saltedPass.toCharArray());
    return hash;
  }
}
