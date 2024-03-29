package com.tehang.common.utility;

import com.tehang.common.infrastructure.exceptions.ParameterException;
import net.sourceforge.pinyin4j.PinyinHelper;
import net.sourceforge.pinyin4j.format.HanyuPinyinCaseType;
import net.sourceforge.pinyin4j.format.HanyuPinyinOutputFormat;
import net.sourceforge.pinyin4j.format.HanyuPinyinToneType;
import net.sourceforge.pinyin4j.format.exception.BadHanyuPinyinOutputFormatCombination;
import org.apache.commons.lang3.StringUtils;

/**
 * 处理汉字转拼音的工具类
 */
public final class PinyinUtils {

  private static final int FIRST_INDEX = 0;

  private PinyinUtils() {
    //do nothing
  }

  /**
   * 翻译汉字字符串为拼音
   *
   * @param target 字符串
   * @return 翻译过后的字符串
   */
  public static String translate(String target) {
    if (StringUtils.isBlank(target)) {
      return StringUtils.EMPTY;
    }

    HanyuPinyinOutputFormat format = new HanyuPinyinOutputFormat();
    format.setCaseType(HanyuPinyinCaseType.UPPERCASE);
    format.setToneType(HanyuPinyinToneType.WITHOUT_TONE);

    try {
      return PinyinHelper.toHanYuPinyinString(target, format, "", true);
    } catch (BadHanyuPinyinOutputFormatCombination ex) {
      throw new ParameterException("拼音转换错误", ex);
    }
  }

  /**
   * 翻译汉字字符串转为短拼音
   * @param target 汉字字符串
   * @return 短拼音
   */
  public static String translateToShortPingYin(String target) {
    StringBuilder sb = new StringBuilder();

    if (StringUtils.isNotBlank(target)) {
      for (int i = 0; i < target.length(); i++) {
        char singleChinese = target.charAt(i);
        String singlePinyin = translate(String.valueOf(singleChinese));

        if (StringUtils.isNotEmpty(singlePinyin)) {
          sb.append(singlePinyin.charAt(FIRST_INDEX));
        }
      }
    }

    return sb.toString();
  }
}
