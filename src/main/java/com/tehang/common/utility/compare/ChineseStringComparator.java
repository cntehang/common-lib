package com.tehang.common.utility.compare;

import java.text.Collator;
import java.util.Comparator;
import java.util.Locale;

/** 中文字符串的比较器，按拼音顺序排序。*/
public class ChineseStringComparator implements Comparator<String> {

  public static final ChineseStringComparator INSTANCE = new ChineseStringComparator();

  private final Collator collator;

  public ChineseStringComparator() {
    this.collator = Collator.getInstance(Locale.CHINA);
  }

  @Override
  public int compare(String str1, String str2) {
    return collator.compare(str1, str2);
  }
}
