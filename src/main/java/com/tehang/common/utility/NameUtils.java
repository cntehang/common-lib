package com.tehang.common.utility;

import com.tehang.common.infrastructure.exceptions.ParameterException;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import java.util.Arrays;
import java.util.List;

/**
 * 处理名字相关的工具类
 */
@SuppressWarnings("PMD.UseObjectForClearerAPI")
@Slf4j
public final class NameUtils {

  private static final List<String> FU_XING_LIST = Arrays.asList("欧阳", "太史", "端木", "上官", "司马", "东方", "独孤",
      "南宫", "万俟", "闻人", "夏侯", "诸葛", "尉迟", "公羊", "赫连", "澹台", "皇甫", "宗政", "濮阳", "公冶", "太叔", "申屠",
      "公孙", "慕容", "仲孙", "钟离", "长孙", "宇文", "司徒", "鲜于", "司空", "闾丘", "子车", "亓官", "司寇", "巫马", "公西",
      "颛孙", "壤驷", "公良", "漆雕", "乐正", "宰父", "谷梁", "拓跋", "夹谷", "轩辕", "令狐", "段干", "百里", "呼延", "东郭",
      "南门", "羊舌", "微生", "公户", "公玉", "公仪", "梁丘", "公仲", "公上", "公门", "公山", "公坚", "左丘", "公伯", "西门",
      "公祖", "第五", "公乘", "贯丘", "公皙", "南荣", "东里", "东宫", "仲长", "子书", "子桑", "即墨", "达奚", "褚师", "吴铭");

  private NameUtils() {
    //do nothing
  }

  /**
   * 检查是否为中文名
   *
   * @param name 姓名
   */
  public static boolean isChineseName(String name) {
    return name != null && name.matches(RegexConstant.CHINESE_NAME_PATTERN);
  }

  /**
   * 检查是否为中文名，允许名字带拼音
   *
   * @param name 姓名
   */
  public static boolean isChineseNameWithPinyin(String name) {
    return name != null && name.matches(RegexConstant.CHINESE_NAME_WITH_PINYIN_PATTERN);
  }

  /**
   * 检查是否为中文名(姓名之间以/分隔)
   *
   * @param name 姓名
   */
  public static boolean isChineseNameWithSplitter(String name) {
    return name != null && name.matches(RegexConstant.CHINESE_NAME_SPLIT_PATTERN);
  }

  /**
   * 检查是否为中文
   *
   * @param name 字符串
   */
  public static boolean isChinese(String name) {
    return name != null && name.matches(RegexConstant.CHINESE_PATTERN);
  }

  /**
   * 检查是否为中文
   *
   * @param name 字符串
   */
  public static boolean containsChinese(String name) {
    return name != null && name.matches(RegexConstant.CONTAINS_CHINESE_PATTERN);
  }

  /**
   * 根据中文姓名字符串获取姓（中文）
   *
   * @param name 姓名字符串
   * @return 姓（中文）
   */
  public static String getSurnameCn(String name) {
    if (!isChineseName(name)) {
      log.debug("Name: {} is not a qualified chinese name", name);
      throw new ParameterException("请指定中文姓名");
    }
    return captureSurnameCn(name);
  }

  /**
   * 根据中文姓名字符串获取名（中文）
   *
   * @param name 姓名字符串
   * @return 名（中文）
   */
  public static String getGivenNameCn(String name) {
    if (!isChineseName(name)) {
      log.debug("Name: {} is not a qualified chinese name", name);
      throw new ParameterException("请指定中文姓名");
    }
    return captureGivenNameCn(name);
  }

  /**
   * 是否为英文姓或名，支持大小写英文、空格
   *
   * @param name 姓名
   * @return true 若满足条件
   */
  public static boolean isEnglish(String name) {
    return name != null && name.matches(RegexConstant.ENGLISH_WITH_BLANK_PATTERN);
  }

  /**
   * 是否为英文姓或名，姓名以斜杠分隔，支持大小写英文、空格
   *
   * @param name 姓名
   * @return true 若满足条件
   */
  public static boolean isEnglishNameWithSplitter(String name) {
    return name != null && name.matches(RegexConstant.SPLIT_ENGLISH_NAME_PATTERN);
  }

  /**
   * 翻译中文名为拼音，姓名之间以"/"分隔，可自动识别复姓
   */
  public static String translateChineseNameToPinyin(String name) {
    String result = name;
    if (isChineseName(name)) {
      log.debug("Name: {} is chinese name, do translate", name);
      String surName = captureSurnameCn(name);
      String surNamePinyin = PinyinUtils.translate(surName);
      String givenName = captureGivenNameCn(name);
      String givenNamePinyin = PinyinUtils.translate(givenName);
      result = surNamePinyin + "/" + givenNamePinyin;
    } else {
      log.debug("Name: {} is english name, no need to translate", name);
    }

    log.debug("Name translation result: {}", result);
    return result;
  }

  private static String captureGivenNameCn(String name) {
    String result;
    if (startWithFuxing(name)) {
      log.debug("Name: {} is start with fu xing, subtract surnameCn from 2", name);
      result = name.substring(2);
    } else {
      log.debug("Name: {} is not start with fu xing, subtract surnameCn from 1", name);
      result = name.substring(1);
    }
    return result;
  }

  private static String captureSurnameCn(String name) {
    String result;
    if (startWithFuxing(name)) {
      log.debug("Name: {} is start with fu xing, subtract 0-2 as surnameCn", name);
      result = name.substring(0, 2);
    } else {
      log.debug("Name: {} is not start with fu xing, subtract 0-1 as surnameCn", name);
      result = name.substring(0, 1);
    }
    return result;
  }

  /**
   * 是否为复姓打头
   *
   * @param name 姓名
   * @return true 若复姓开头
   */
  public static boolean startWithFuxing(String name) {
    boolean isFuXing = false;
    if (StringUtils.isNotBlank(name) && name.length() >= 2) {
      log.trace("Name: {} length greater than 2, do check", name);
      String possibleFuxing = name.substring(0, 2);
      isFuXing = FU_XING_LIST.contains(possibleFuxing);
    }
    log.trace("Check is name: {} start with fuxing, result: {}", name, isFuXing);
    return isFuXing;
  }

  /**
   * 客人姓名是中文姓名时:lastName=拼音姓名，比如 lastName=ZHANG SANSI 要求:拼音姓和拼音名之间以空格分隔
   * 客人姓名是英文姓名时:lastName=Michael Jackson
   */
  public static String toFirstName(String raw) {
    if (StringUtils.isBlank(raw)) {
      throw new ParameterException("不能为空");
    }
    if (isEnglishNameWithSplitter(raw)) {
      return StringUtils.replace(raw, ThStringUtils.SPLITTER, ThStringUtils.SPACE);
    } else {
      return raw;
    }
  }

  /**
   * 客人姓名是中文姓名时:lastName=拼音姓名，比如 lastName=ZHANG SANSI 要求:拼音姓和拼音名之间以空格分隔
   * 客人姓名是英文姓名时:lastName=Michael Jackson
   */
  public static String toLastName(String raw) {
    if (StringUtils.isBlank(raw)) {
      throw new ParameterException("不能为空");
    }
    String resultWithSplitter;
    if (isEnglishNameWithSplitter(raw)) {
      resultWithSplitter = raw;
    } else {
      resultWithSplitter = translateChineseNameToPinyin(raw);
    }

    return StringUtils.replace(resultWithSplitter, ThStringUtils.SPLITTER, ThStringUtils.SPACE);
  }
}
