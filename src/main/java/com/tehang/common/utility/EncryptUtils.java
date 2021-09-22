package com.tehang.common.utility;

import com.tehang.common.infrastructure.exceptions.ParameterException;
import com.tehang.common.infrastructure.exceptions.SystemErrorException;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

/**
 * 加密解密工具类
 */
@Slf4j
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class EncryptUtils {

  /**
   * 生成 MD5
   *
   * @param data 待处理数据
   * @return MD5结果
   */
  public static String md5(String data) {
    if (data == null) {
      throw new ParameterException("生成md5出现错误，入参不为空");
    }

    StringBuilder sb = new StringBuilder();
    try {
      MessageDigest md = MessageDigest.getInstance("MD5");
      byte[] array = md.digest(data.getBytes("UTF-8"));
      for (byte item : array) {
        sb.append(Integer.toHexString((item & 0xFF) | 0x100), 1, 3);
      }
    } catch (NoSuchAlgorithmException | UnsupportedEncodingException ex) {
      log.warn("md5 error: {}", ex.getMessage(), ex);
      throw new SystemErrorException("md5 error: " + ex.getMessage(), ex);
    }
    return sb.toString();
  }

  /**
   * 生成签名: 按指定的数据按key字母排序进行拼接，key拼接在最后，进行md5签名，并转成小写
   *
   * @param data 待签名数据
   * @param signKey 签名key
   * @return 签名
   */
  public static String generateMd5Signature(final Map<String, Object> data, String signKey) {

    Set<String> keySet = data.keySet();
    String[] keyArray = keySet.toArray(new String[keySet.size()]);

    //按Key字母顺序排列
    Arrays.sort(keyArray);

    StringBuffer buffer = new StringBuffer();

    //拼接参数： a=xxx&b=xxxx
    for (String key : keyArray) {
      Object value = data.get(key);
      String valueString = value == null ? StringUtils.EMPTY : value.toString();

      if (StringUtils.isNotEmpty(valueString)) {
        if (buffer.length() > 0) {
          buffer.append('&');
        }
        buffer.append(String.format("%s=%s", key, value));
      }
    }

    //key拼在最后
    buffer.append(signKey);

    String sign = md5(buffer.toString());
    //签名转成小写
    String result = sign.toLowerCase(Locale.US);

    log.debug("exit generateMd5Signature, source: {}, md5: {}", buffer.toString(), result);
    return result;
  }
}