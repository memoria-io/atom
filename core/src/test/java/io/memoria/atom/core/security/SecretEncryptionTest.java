package io.memoria.atom.core.security;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;

import javax.crypto.NoSuchPaddingException;
import java.security.GeneralSecurityException;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;

class SecretEncryptionTest {
  private static final String encryptionKey = "HelloWorld";
  private static final String salt = "HelloHello";
  private static final String originalPassword = "mypassword";

  private static final SecretEncryption SECRET_ENCRYPTION;

  static {
    try {
      SECRET_ENCRYPTION = new SecretEncryption(encryptionKey, salt);

    } catch (NoSuchPaddingException | NoSuchAlgorithmException | InvalidKeySpecException e) {
      throw new RuntimeException(e);
    }
  }

  @Test
  void encryptAndDecrypt() throws GeneralSecurityException {
    var encPassword = SECRET_ENCRYPTION.encrypt(originalPassword);
    var decPassword = SECRET_ENCRYPTION.decrypt(encPassword);
    Assertions.assertThat(decPassword).isEqualTo(originalPassword);
  }
}
