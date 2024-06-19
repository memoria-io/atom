package io.memoria.atom.core.security;

import io.memoria.atom.core.file.ConfigFileOps;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;

import javax.crypto.NoSuchPaddingException;
import java.io.IOException;
import java.security.GeneralSecurityException;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;

class SecretEncryptionTest {
  private static final ConfigFileOps configOps = new ConfigFileOps("#include:", true);
  private static final String encryptionKey;
  private static final String salt;
  private static final SecretEncryption SECRET_ENCRYPTION;

  static {
    try {
      var file = configOps.read("file/security/systemEnv.yaml");
      var lines = file.split("\n");
      encryptionKey = lines[0].replace("encKey:", "").trim();
      salt = lines[1].replace("encSalt:", "").trim();
      System.out.println(encryptionKey);
      System.out.println(salt);
      SECRET_ENCRYPTION = new SecretEncryption(encryptionKey, salt);
    } catch (NoSuchPaddingException | NoSuchAlgorithmException | InvalidKeySpecException | IOException e) {
      throw new RuntimeException(e);
    }
  }

  @Test
  void encryptAndDecrypt() throws GeneralSecurityException {
    var secret = "xh6T0iZcA1UsghjUWarnpHz6D_4ZPKNxp7kJsj_cfDLCTUppjSiieelIgZr4-g3P";
    var encSecret = SECRET_ENCRYPTION.encrypt(secret);
    System.out.println(encSecret);
    var decSecret = SECRET_ENCRYPTION.decrypt(encSecret);
    Assertions.assertThat(decSecret).isEqualTo(secret);
  }
}
