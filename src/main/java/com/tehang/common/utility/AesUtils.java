package com.tehang.common.utility;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;

/**
 * AES加密解密工具类.
 */
@Slf4j
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class AesUtils {

  private final static String ALGORITHM = "AES";

  /**
   * 加密.
   */
  @SneakyThrows
  public static String encrypt(String data, String secret) {
    byte[] key = asBin(secret);
    SecretKeySpec sKey = new SecretKeySpec(key, ALGORITHM);

    Cipher cipher = Cipher.getInstance(ALGORITHM);
    cipher.init(Cipher.ENCRYPT_MODE, sKey);
    byte[] encrypted = cipher.doFinal(data.getBytes());
    return asHex(encrypted);
  }

  /**
   * 解密.
   */
  @SneakyThrows
  public static String decrypt(String data, String secret) {
    byte[] tmp = asBin(data);
    byte[] key = asBin(secret);
    SecretKeySpec sKey = new SecretKeySpec(key, ALGORITHM);
    Cipher cipher = Cipher.getInstance(ALGORITHM);
    cipher.init(Cipher.DECRYPT_MODE, sKey);
    byte[] decrypted = cipher.doFinal(tmp);
    return new String(decrypted);
  }

  /**
   * 转为十六进制
   */
  private static String asHex(byte[] buf) {
    StringBuilder strBuf = new StringBuilder(buf.length * 2);
    int i;

    for (i = 0; i < buf.length; i++) {
      if (((int) buf[i] & 0xff) < 0x10) {
        strBuf.append("0");
      }
      strBuf.append(Long.toString((int) buf[i] & 0xff, 16));
    }

    return strBuf.toString().toUpperCase();
  }

  /**
   * 转为二进制
   */
  private static byte[] asBin(String src) {
    if (src.length() < 1) {
      throw new IllegalArgumentException("转为二进制时，字符串参数的长度不能小于1");
    }

    byte[] encrypted = new byte[src.length() / 2];

    for (int i = 0; i < src.length() / 2; i++) {
      int high = Integer.parseInt(src.substring(i * 2, i * 2 + 1), 16);
      int low = Integer.parseInt(src.substring(i * 2 + 1, i * 2 + 2), 16);
      encrypted[i] = (byte) (high * 16 + low);
    }

    return encrypted;
  }
}
