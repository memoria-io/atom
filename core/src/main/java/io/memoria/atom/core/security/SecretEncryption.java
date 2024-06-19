package io.memoria.atom.core.security;

import javax.crypto.Cipher;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.AlgorithmParameters;
import java.security.GeneralSecurityException;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.util.Base64;

/**
 * <a href="https://stackoverflow.com/questions/1132567/encrypt-password-in-configuration-files">Reference</a>
 */
public class SecretEncryption {

  private final EncryptionConfig config;
  private final Cipher cipher;
  private final SecretKeyFactory keyFactory;
  private final SecretKeySpec encryptionKey;

  public SecretEncryption(String encryptionKey, String salt)
          throws NoSuchPaddingException, NoSuchAlgorithmException, InvalidKeySpecException {
    this(EncryptionConfig.create(), encryptionKey, salt);
  }

  public SecretEncryption(EncryptionConfig config, String encryptionKey, String salt)
          throws NoSuchPaddingException, NoSuchAlgorithmException, InvalidKeySpecException {
    this.config = config;
    this.keyFactory = SecretKeyFactory.getInstance(config.keyAlg());
    this.cipher = Cipher.getInstance(config.cipherAlg());
    this.encryptionKey = createSecretKey(encryptionKey.toCharArray(), salt.getBytes());
  }

  public String encrypt(String password) throws GeneralSecurityException {
    cipher.init(Cipher.ENCRYPT_MODE, this.encryptionKey);
    AlgorithmParameters parameters = cipher.getParameters();
    IvParameterSpec ivParameterSpec = parameters.getParameterSpec(IvParameterSpec.class);
    byte[] cryptoText = cipher.doFinal(password.getBytes(StandardCharsets.UTF_8));
    byte[] iv = ivParameterSpec.getIV();
    return STR."\{base64Encode(iv)}:\{base64Encode(cryptoText)}";
  }

  public String decrypt(String encryptedPassword) throws GeneralSecurityException {
    String iv = encryptedPassword.split(":")[0];
    String property = encryptedPassword.split(":")[1];
    cipher.init(Cipher.DECRYPT_MODE, encryptionKey, new IvParameterSpec(base64Decode(iv)));
    return new String(cipher.doFinal(base64Decode(property)), StandardCharsets.UTF_8);
  }

  public EncryptionConfig getConfig() {
    return config;
  }

  SecretKeySpec createSecretKey(char[] password, byte[] salt) throws InvalidKeySpecException {
    PBEKeySpec keySpec = new PBEKeySpec(password, salt, config.iterationCount(), config.keyLength());
    SecretKey keyTmp = keyFactory.generateSecret(keySpec);
    return new SecretKeySpec(keyTmp.getEncoded(), config.secretKeySpecAlg());
  }

  String base64Encode(byte[] bytes) {
    return Base64.getEncoder().encodeToString(bytes);
  }

  byte[] base64Decode(String property) {
    return Base64.getDecoder().decode(property);
  }

}