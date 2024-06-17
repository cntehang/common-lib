package com.tehang.common.utility.db;

import java.util.Stack;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class SQLParser {

  private static final Pattern SELECT_PATTERN = Pattern.compile("\\bSELECT\\b", Pattern.CASE_INSENSITIVE);
  private static final Pattern FROM_PATTERN = Pattern.compile("\\bFROM\\b", Pattern.CASE_INSENSITIVE);

  /** 查找sql语句中最外层的from关键字的位置索引 */
  public static int findFromIndexForSql(String sql) {
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

  public static void main(String[] args) {
    String sql = "SELECT * FROM (SELECT a FROM b WHERE c = 'SELECT d FROM e') f";
    int fromPosition = findFromIndexForSql(sql);
    System.out.println("The position of the matching FROM is: " + fromPosition);

    String sql2 = "SELECT a, (select * from b) FROM (SELECT a FROM b WHERE c = 'SELECT d FROM e') f";
    int fromPosition2 = findFromIndexForSql(sql2);
    System.out.println("The position2 of the matching FROM is: " + fromPosition2);
  }
}
