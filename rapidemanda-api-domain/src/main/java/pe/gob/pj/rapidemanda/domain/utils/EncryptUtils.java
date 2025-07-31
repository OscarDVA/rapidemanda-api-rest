package pe.gob.pj.rapidemanda.domain.utils;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.security.GeneralSecurityException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import javax.crypto.Cipher;
import javax.crypto.CipherOutputStream;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.PBEParameterSpec;
import javax.crypto.spec.SecretKeySpec;

import pe.gob.pj.rapidemanda.domain.utils.crypt.Base64u;

import java.util.Base64;
import java.util.logging.Level;
import java.util.logging.Logger;

public class EncryptUtils {

  static final Logger logger = Logger.getLogger(EncryptUtils.class.getName());

  private static final String defaultKey = "qweiuierwo";
  private static final byte[] SALT = {(byte) 0xde, (byte) 0x33, (byte) 0x10, (byte) 0x12,
      (byte) 0xde, (byte) 0x33, (byte) 0x10, (byte) 0x12};

  public static String MD5 = "MD5";
  public static String SHA512 = "SHA-512";

  /**
   * Convert array of byte to hexadecimal
   * 
   * @param digest Array of Byte
   * @return The String hexadecimal
   */
  private static String toHexadecimal(byte[] digest) {
    StringBuilder hash = new StringBuilder();
    for (byte b : digest) {
      int v = b & 0xFF;
      if (Integer.toHexString(v).length() == 1) {
        hash.append('0');
      }
      hash.append(Integer.toHexString(v));
    }
    return hash.toString();
  }

  /**
   * Convert array of byte to hexadecimal 5012
   * 
   * @param digest Array of byte
   * @return The string hexadecimal 512
   */
  private static String toHexadecimal512(byte[] digest) {
    StringBuilder hash = new StringBuilder();
    for (byte b : digest) {
      int v = b & 0xFF;
      if (Integer.toHexString(v).length() == 1) {
        hash.append("00");
      }
      hash.append(Integer.toHexString(v));
    }
    return hash.toString();
  }

  /**
   * Encryption MD5
   * 
   * @param message The message to encrypt
   * @return The encrypted message
   */
  public static String encryptMd5Hash(String message) {
    try {
      MessageDigest messageDigest = MessageDigest.getInstance(MD5);
      messageDigest.update(message.getBytes());
      return toHexadecimal(messageDigest.digest());
    } catch (NoSuchAlgorithmException ex) {
      logger.log(Level.WARNING, "Error creating digest MD5", ex);
      return null;
    }
  }

  /**
   * Encryption SHA512
   * 
   * @param message The message to encrypt
   * @return The encrypted message
   */
  public static String encryptSHA512Hash(String message) {
    try {
      MessageDigest messageDigest = MessageDigest.getInstance(SHA512);
      messageDigest.update(message.getBytes());
      return toHexadecimal512(messageDigest.digest());
    } catch (NoSuchAlgorithmException ex) {
      logger.log(Level.WARNING, "Error creating digest SHA512", ex);
      return null;
    }
  }

  /**
   * Custom encryption for two messages
   * 
   * @param firstMessage The first message
   * @param secondMessage The second message
   * @return The encrypted message
   */
  public static String encrypt(String firstMessage, String secondMessage) {
    return encryptSHA512Hash(encryptMd5Hash(firstMessage.toLowerCase()) + secondMessage);
  }

  public static String encryptPastFrass(String property)
      throws GeneralSecurityException, UnsupportedEncodingException {
    SecretKeyFactory keyFactory = SecretKeyFactory.getInstance("PBEWithMD5AndDES");
    SecretKey key = keyFactory
        .generateSecret(new PBEKeySpec(ProjectProperties.getSeguridadSecretToken().toCharArray()));
    Cipher pbeCipher = Cipher.getInstance("PBEWithMD5AndDES");
    pbeCipher.init(Cipher.ENCRYPT_MODE, key, new PBEParameterSpec(SALT, 20));
    return base64Encode(pbeCipher.doFinal(property.getBytes("UTF-8")));
  }

  public static String decryptPastFrass(String property)
      throws GeneralSecurityException, IOException {
    SecretKeyFactory keyFactory = SecretKeyFactory.getInstance("PBEWithMD5AndDES");
    SecretKey key = keyFactory
        .generateSecret(new PBEKeySpec(ProjectProperties.getSeguridadSecretToken().toCharArray()));
    Cipher pbeCipher = Cipher.getInstance("PBEWithMD5AndDES");
    pbeCipher.init(Cipher.DECRYPT_MODE, key, new PBEParameterSpec(SALT, 20));
    return new String(pbeCipher.doFinal(base64Decode(property)), "UTF-8");
  }

  public static String encryptPastFrass(String property, char[] KEY)
      throws GeneralSecurityException, UnsupportedEncodingException {
    SecretKeyFactory keyFactory = SecretKeyFactory.getInstance("PBEWithMD5AndDES");
    SecretKey key = keyFactory.generateSecret(new PBEKeySpec(KEY));
    Cipher pbeCipher = Cipher.getInstance("PBEWithMD5AndDES");
    pbeCipher.init(Cipher.ENCRYPT_MODE, key, new PBEParameterSpec(SALT, 20));
    return base64Encode(pbeCipher.doFinal(property.getBytes("UTF-8")));
  }

  public static String decryptPastFrass(String property, char[] KEY)
      throws GeneralSecurityException, IOException {
    SecretKeyFactory keyFactory = SecretKeyFactory.getInstance("PBEWithMD5AndDES");
    SecretKey key = keyFactory.generateSecret(new PBEKeySpec(KEY));
    Cipher pbeCipher = Cipher.getInstance("PBEWithMD5AndDES");
    pbeCipher.init(Cipher.DECRYPT_MODE, key, new PBEParameterSpec(SALT, 20));
    return new String(pbeCipher.doFinal(base64Decode(property)), "UTF-8");
  }

  private static String base64Encode(byte[] bytes) {
    return Base64.getEncoder().encodeToString(bytes);
  }

  private static byte[] base64Decode(String property) {
    return Base64.getDecoder().decode(property);
  }

  public static String cryptBase64u(String input, int mode) throws Exception {
    Base64u base64u = new Base64u();
    base64u.setLineLength(72);

    KeyGenerator kgen = KeyGenerator.getInstance("Blowfish");
    kgen.init(448);
    byte[] raw = defaultKey.getBytes("UTF-8");
    SecretKeySpec skeySpec = new SecretKeySpec(raw, "Blowfish");

    Cipher cipher = Cipher.getInstance("Blowfish/ECB/PKCS5Padding");
    cipher.init(mode, skeySpec);

    ByteArrayOutputStream bos = new ByteArrayOutputStream();
    ByteArrayInputStream bis;
    if (Cipher.ENCRYPT_MODE == mode) {
      bis = new ByteArrayInputStream(input.getBytes());
    } else {
      bis = new ByteArrayInputStream(base64u.decode(input));
    }
    CipherOutputStream cos = new CipherOutputStream(bos, cipher);

    int length;
    byte[] buffer = new byte[8192];
    while ((length = bis.read(buffer)) != -1) {
      cos.write(buffer, 0, length);
    }

    bis.close();
    cos.close();

    if (Cipher.ENCRYPT_MODE == mode) {
      return base64u.encode(bos.toByteArray());
    } else {
      return bos.toString();
    }
  }
}
