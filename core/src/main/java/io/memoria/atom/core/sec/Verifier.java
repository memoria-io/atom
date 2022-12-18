package io.memoria.atom.core.sec;

public interface Verifier {
  boolean verify(String password, String hash, String salt);
}
