package io.memoria.atom.core.security;

import io.memoria.atom.core.file.ConfigFile;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;

import javax.crypto.NoSuchPaddingException;
import java.io.IOException;
import java.security.GeneralSecurityException;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;

class SecretEncryptionTest {
  private static final String encryptionKey;
  private static final String salt;
  private static final SecretEncryption SECRET_ENCRYPTION;

  static {
    try {
      var lines = new ConfigFile("file/security/systemEnv.yaml").readLines().toList();
      encryptionKey = lines.getFirst().replace("encKey:", "").trim();
      salt = lines.get(1).replace("encSalt:", "").trim();
      SECRET_ENCRYPTION = new SecretEncryption(encryptionKey, salt);
    } catch (NoSuchPaddingException | NoSuchAlgorithmException | InvalidKeySpecException | IOException e) {
      throw new RuntimeException(e);
    }
  }

  @Test
  void encryptAndDecrypt() throws GeneralSecurityException {
    var secret = "some_secret";
    var encSecret = SECRET_ENCRYPTION.encrypt(secret);
    //    System.out.println(encSecret);
    var decSecret = SECRET_ENCRYPTION.decrypt(encSecret);
    Assertions.assertThat(decSecret).isEqualTo(secret);
  }
}
