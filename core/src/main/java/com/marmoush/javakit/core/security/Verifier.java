package com.marmoush.javakit.core.security;

public interface Verifier {
  boolean verify(String password, String hash, String salt);
}
