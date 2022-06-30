package com.tehang.common.utility;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

/**
 * 身份证校验工具
 */
@Slf4j
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class IdCardCheckUtils {

  private static final int ID_CARD_LENGTH = 18;
  private static final int[] weight = {7, 9, 10, 5, 8, 4, 2, 1, 6, 3, 7, 9, 10, 5, 8, 4, 2};    //十七位数字本体码权重
  private static final char[] validate = {'1', '0', 'X', '9', '8', '7', '6', '5', '4', '3', '2'};    //mod11,对应校验码字符值

  /**
   * 查询身份证号码是否有效
   *
   * @param idCardNo 身份证号码
   * @return true 若身份证号码有效
   */
  public static boolean isIdCardValid(String idCardNo) {
    try {
      boolean isValid = false;
      boolean isNotBlank = StringUtils.isNotBlank(idCardNo);
      if (isNotBlank) {
        boolean isLengthRight = idCardNo.length() == ID_CARD_LENGTH;
        if (isLengthRight) {
          char validateCode = getValidateCode(idCardNo.substring(0, 17));
          isValid = validateCode == idCardNo.substring(17, 18).charAt(0);
        }
      }
      return isValid;
    }
    catch (NumberFormatException ex) {
      return false;
    }
  }

  /**
   * 根据身份证前 17 位计算第 18 位校验码
   *
   * @param id17 身份证前 17 位
   * @return 第 18 位校验码
   */
  private static char getValidateCode(String id17) {
    int sum = 0;
    int mode = 0;
    for (int i = 0; i < id17.length(); i++) {
      sum = sum + Integer.parseInt(String.valueOf(id17.charAt(i))) * weight[i];
    }
    mode = sum % 11;
    return validate[mode];
  }
}
