package com.tehang.common.utility.consts;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

/**
 * 通用正则表达式常量类
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class RegexConst {

  /**
   * 手机号码正则
   */
  public static final String MOBILE_PATTERN = "^1[0-9]{10}$";

  /**
   * 手机号码正则(允许为空)
   */
  public static final String MOBILE_OR_EMPTY_PATTERN = "^1[0-9]{10}$|";

  /**
   * 邮箱正则
   */
  public static final String EMAIL_PATTERN = "[\\w!#$%&'*+/=?^_`{|}~-]+(?:\\.[\\w!#$%&'*+/=?^_`{|}~-]+)*@(?:[\\w](?:[\\w-]*[\\w])?\\.)+[\\w](?:[\\w-]*[\\w])?";

  /**
   * 匹配邮箱或者空正则
   */
  public static final String EMAIL_OR_EMPTY_PATTERN = EMAIL_PATTERN + "|";

  /**
   * 性别正则或空字符串
   */
  public static final String GENDER_PATTERN = "(?i)^(MALE|FEMALE)$";

  /**
   * 中文性别正则或空字符串
   */
  public static final String CHINESE_GENDER_PATTERN = "(?i)^(男|女)$";

  /**
   * 中文称呼正则
   */
  public static final String CHINESE_TITLE_PATTERN = "(?i)^(先生|女士)$";

  /**
   * 性别正则
   */
  public static final String GENDER_OR_EMPTY_PATTERN = GENDER_PATTERN + "|";

  /**
   * 中国18位身份证正则
   */
  public static final String CHINESE_CARD_ID_PATTERN = "^[1-9]\\d{5}(18|19|([23]\\d))\\d{2}((0[1-9])|(10|11|12))(([0-2][1-9])|10|20|30|31)\\d{3}[0-9Xx]$";
}
