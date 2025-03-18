package io.memoria.atom.core.security;

/**
 * <a href="https://stackoverflow.com/questions/1132567/encrypt-password-in-configuration-files">Reference</a>
 */
public record EncryptionConfig(String keyAlg,
                               String cipherAlg,
                               String secretKeySpecAlg,
                               int iterationCount,
                               int keyLength) {
  public static final String DEFAULT_KEY_ALG = "PBKDF2WithHmacSHA512";
  public static final String DEFAULT_CIPHER_ALG = "AES/CBC/PKCS5Padding";
  public static final String DEFAULT_SECRET_KEY_SPEC_ALG = "AES";
  // Decreasing this speeds down startup time and can be useful during testing, but it also makes it easier for brute force attackers
  public static final int DEFAULT_ITERATION_COUNT = 40000;
  // Other values give me java.security.InvalidKeyException: Illegal key size or default parameters
  public static final int DEFAULT_KEY_LENGTH = 128;

  /**
   * @return new EncryptionConfig instance with default settings
   */
  public static EncryptionConfig create() {
    return new EncryptionConfig(DEFAULT_KEY_ALG,
                                DEFAULT_CIPHER_ALG,
                                DEFAULT_SECRET_KEY_SPEC_ALG,
                                DEFAULT_ITERATION_COUNT,
                                DEFAULT_KEY_LENGTH);
  }

  public EncryptionConfig withIterationCount(int iterationCount) {
    return new EncryptionConfig(this.keyAlg, this.cipherAlg, this.secretKeySpecAlg, iterationCount, this.keyLength);
  }
}
