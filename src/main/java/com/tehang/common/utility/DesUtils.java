package com.tehang.common.utility;

import com.tehang.common.infrastructure.exceptions.SystemErrorException;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.tomcat.util.codec.binary.Base64;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.DESKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.SecureRandom;

/**
 * DES加密解密工具类.
 */
@Slf4j
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class DesUtils {

  private final static String ALGORITHM = "DES";
  private final static int EVEN_NUMBER = 2;

  /**
   * DESC加密并转为Base64编码.
   */
  public static String encryptBase64(String data, String key) {
    byte[] bt = encrypt(data.getBytes(StandardCharsets.UTF_8), key.getBytes());
    return Base64.encodeBase64String(bt);
  }

  /**
   * Base64解码后进行DES解密.
   */
  public static String decryptBase64(String data, String key) {
    byte[] buf = Base64.decodeBase64(data);
    byte[] bt = decrypt(buf, key.getBytes());
    return new String(bt, StandardCharsets.UTF_8);
  }

  /**
   * DES加密.
   */
  public static String encrypt(String plainText, String secretKey) {
    try {
      return byte2hex(encrypt(plainText.getBytes(StandardCharsets.UTF_8), secretKey.getBytes()));
    }
    catch (Exception ex) {
      throw new SystemErrorException(String.format("DES加密失败，原因:%s", ex.getMessage()), ex);
    }
  }

  @SneakyThrows
  private static byte[] encrypt(byte[] src, byte[] key) {
    SecureRandom sr = new SecureRandom();
    DESKeySpec dks = new DESKeySpec(key);
    SecretKeyFactory keyFactory = SecretKeyFactory.getInstance(ALGORITHM);
    SecretKey secretKey = keyFactory.generateSecret(dks);
    Cipher cipher = Cipher.getInstance(ALGORITHM);
    cipher.init(Cipher.ENCRYPT_MODE, secretKey, sr);
    return cipher.doFinal(src);
  }

  /**
   * DES解密.
   */
  public static String decrypt(byte[] cipherText, String secretKey) {
    try {
      return new String(decrypt(hex2byte(cipherText), secretKey.getBytes()), StandardCharsets.UTF_8);
    }
    catch (Exception ex) {
      throw new SystemErrorException(String.format("DES加密失败，原因:%s", ex.getMessage()), ex);
    }
  }

  @SneakyThrows
  private static byte[] decrypt(byte[] src, byte[] key) {
    SecureRandom sr = new SecureRandom();
    DESKeySpec dks = new DESKeySpec(key);
    SecretKeyFactory keyFactory = SecretKeyFactory.getInstance(ALGORITHM);
    SecretKey secretKey = keyFactory.generateSecret(dks);
    Cipher cipher = Cipher.getInstance(ALGORITHM);
    cipher.init(Cipher.DECRYPT_MODE, secretKey, sr);
    return cipher.doFinal(src);
  }

  private static byte[] hex2byte(byte[] decryptedBytes) {
    if (decryptedBytes.length % EVEN_NUMBER != 0) {
      throw new IllegalArgumentException("长度不是偶数");
    }

    byte[] bytes = new byte[decryptedBytes.length / EVEN_NUMBER];

    for (int n = 0; n < bytes.length; n += EVEN_NUMBER) {
      String item = new String(bytes, n, EVEN_NUMBER);
      bytes[n / EVEN_NUMBER] = (byte)Integer.parseInt(item, 16);
    }

    return bytes;
  }

  private static String byte2hex(byte[] bytes) {
    StringBuilder sb = new StringBuilder();
    String temp;

    for (byte value : bytes) {
      temp = Integer.toHexString(value & 0XFF);
      if (temp.length() == 1) {
        sb.append("0").append(temp);
      }
      else {
        sb.append(temp);
      }
    }

    return sb.toString().toUpperCase();
  }
}
