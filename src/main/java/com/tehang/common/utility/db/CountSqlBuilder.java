package com.tehang.common.utility.db;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.util.Stack;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 构造countSql语句的辅助类。
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class CountSqlBuilder {

  private static final Pattern SELECT_PATTERN = Pattern.compile("\\bSELECT\\b", Pattern.CASE_INSENSITIVE);
  private static final Pattern FROM_PATTERN = Pattern.compile("\\bFROM\\b", Pattern.CASE_INSENSITIVE);

  /**
   * 分页查询时，根据传入的sql，构造countSql。
   */
  public static String buildCountSql(String sql) {
    // 查找sql语句中from关键字的位置
    int fromPosition = findFromPositionForSql(sql);

    // 生成count语句
    return "select count(*) " + removeOrderBy(sql.substring(fromPosition));
  }

  /** 查找sql语句中最外层的from关键字的位置索引 */
  private static int findFromPositionForSql(String sql) {
    String upperSQL = sql.toUpperCase();
    Matcher selectMatcher = SELECT_PATTERN.matcher(upperSQL);
    Matcher fromMatcher = FROM_PATTERN.matcher(upperSQL);

    Stack<Integer> selectStack = new Stack<>();
    int currentIndex = 0; // 当前的搜索位置

    while (true) {
      boolean foundSelect = selectMatcher.find(currentIndex);
      boolean foundFrom = fromMatcher.find(currentIndex);

      if (!foundSelect && !foundFrom) {
        break;
      }

      if (foundSelect && (!foundFrom || selectMatcher.start() < fromMatcher.start())) {
        // 先找到select, 则将select压入堆栈，并更新搜索位置，以继续往后搜索
        selectStack.push(selectMatcher.start());
        currentIndex = selectMatcher.end();
      }
      else {
        // 先找到from, 需要判断是否是匹配第一个select
        if (!selectStack.isEmpty()) {
          selectStack.pop();
          if (selectStack.isEmpty()) {
            // 堆栈为空，说明找到了第一个select匹配的from，返回其位置
            return fromMatcher.start();
          }
        }
        currentIndex = fromMatcher.end();
      }
    }

    throw new IllegalArgumentException("invalid sql statement: " + sql);
  }

  /** 去除order by 提高select count(*)的速度. */
  private static String removeOrderBy(String sql) {
    Pattern pattern = Pattern.compile("order\\s*by[\\w|\\W|\\s|\\S]*", Pattern.CASE_INSENSITIVE);
    Matcher matcher = pattern.matcher(sql);
    StringBuffer sb = new StringBuffer();
    while (matcher.find()) {
      matcher.appendReplacement(sb, "");
    }
    matcher.appendTail(sb);
    return sb.toString();
  }
}
