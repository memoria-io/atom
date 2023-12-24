package io.memoria.atom.core.security;

public interface Verifier {
  boolean verify(String password, String hash, String salt);
}
