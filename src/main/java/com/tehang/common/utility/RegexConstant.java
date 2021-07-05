package com.tehang.common.utility;

public final class RegexConstant {

  /**
   * 手机号码正则.
   */
  public static final String MOBILE_PATTERN = "^1[0-9]{10}$";

  /**
   * 正数类型.
   */
  public static final String INTEGER_PATTERN = "^(-?\\d+)$";

  /**
   * 或符号.
   */
  public static final String OR_SIGNAL = "|";

  /**
   * 英文字符串.
   */
  public static final String ENGLISH_REGEXP = "^[a-zA-Z]*";

  /**
   * 邮箱正则.
   */
  public static final String EMAIL_PATTERN =
    "[\\w!#$%&'*+/=?^_`{|}~-]+(?:\\.[\\w!#$%&'*+/=?^_`{|}~-]+)*@(?:[\\w](?:" + "[\\w-]*[\\w])?\\.)+[\\w](?:[\\w-]*[\\w])?";

  /**
   * 匹配邮箱或者空正则.
   */
  public static final String EMAIL_OR_EMPTY_REGEX = EMAIL_PATTERN + "|";

  /**
   * 手机号码正则，最大限度放宽检验条件.
   */
  public static final String MOBILE_PATTERN_SIMPLIFIED = "^1\\d{10}$";

  /**
   * 手机号码或空字符串正则.
   */
  public static final String MOBILE_OR_EMPTY_REGEX = MOBILE_PATTERN + "|";

  /**
   * 性别正则或空字符串.
   */
  public static final String GENDER_PATTERN = "(?i)^(MALE|FEMALE)$";

  /**
   * 性别正则.
   */
  public static final String GENDER_OR_EMPTY_REGEX = GENDER_PATTERN + "|";

  /**
   * 验证UTC时间格式的正则表达式，可以为空.
   */
  public static final String ISO_DATE_REGEX_OR_EMPTY =
    "(((((19|20)\\d{2})-(0?[13-9]|1[012])-(0?[1-9]|[12]\\d|30))|(((19|20)\\d{2})-(0?[13578]|1[02])-31)"
      + "|(((19|20)\\d{2})-0?2-(0?[1-9]|1\\d|2[0-8]))|((((19|20)([13579][26]|[2468][048]|0[48]))|(2000))-0?2-29))T"
      + "(2[0-3]|[01][0-9]):([0-5][0-9]):([0-5][0-9])(\\.[0-9]{3})Z)|";

  /**
   * 日期正则: yyyy-MM-dd.
   */
  public static final String DATE_PATTERN =
    "(?:(?!0000)[0-9]{4}-(?:(?:0[1-9]|1[0-2])-" + "(?:0[1-9]|1[0-9]|2[0-8])|(?:0[13-9]|1[0-2])-(?:29|30)|(?:0[13578]|1[02])-31)|(?:[0-9]{2}"
      + "(?:0[48]|[2468][048]|[13579][26])|(?:0[48]|[2468][048]|[13579][26])00)-02-29)";

  /**
   * 日期正则: yyyy-MM-dd 或 空串.
   */
  public static final String DATE_PATTERN_OR_EMPTY =
    "(?:(?!0000)[0-9]{4}-(?:(?:0[1-9]|1[0-2])-" + "(?:0[1-9]|1[0-9]|2[0-8])|(?:0[13-9]|1[0-2])-(?:29|30)|(?:0[13578]|1[02])-31)|(?:[0-9]{2}"
      + "(?:0[48]|[2468][048]|[13579][26])|(?:0[48]|[2468][048]|[13579][26])00)-02-29)|";

  /**
   * 日期正则：yyyyMMdd.
   */
  public static final String SHORT_DATE_PATTERN = "\\d{4}(0[1-9]|1[012])(0[1-9]|[12][0-9]|3[01])";

  /**
   * 时间正则: HH:mm:ss.
   */
  public static final String TIME_PATTERN = "(?:[01]\\d|2[0-3])(?::[0-5]\\d){2}";

  /**
   * 短时间正则: HH:mm.
   */
  public static final String SHORT_TIME_PATTERN = "(?:[01]\\d|2[0-3])(?::[0-5]\\d){1}";

  /**
   * 短时期时间正则 [yyyy-MM-dd HH:mm].
   */
  public static final String LOCAL_DATE_TIME_PATTERN = "^((((19|20)\\d{2})-(0?[13-9]|1[012])-(0?[1-9]|[12]\\d|30))|"
    + "(((19|20)\\d{2})-(0?[13578]|1[02])-31)|(((19|20)\\d{2})-0?2-(0?[1-9]|1\\d|2[0-8]))|((((19|20)([13579][26]|"
    + "[2468][048]|0[48]))|(2000))-0?2-29))\\s(2[0-3]|[01][0-9]):([0-5][0-9])(\\\\.[0-9]+)?(Z)?$";

  private RegexConstant() {
    //do nothing
  }
}
