package com.tehang.common.utility.log.operationlog;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.apache.commons.collections4.ListUtils;

import java.util.Collection;
import java.util.Objects;

/**
 * 用户操作日志工具类
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
class UserOperationLogUtils {

  /**
   * 根据字段的旧值和新值，创建变更的属性信息
   */
  public static ChangedAttribute getChangedAttribute(String attributeName, String attributeAlias, Object oldValue, Object newValue) {
    if (Objects.equals(oldValue, newValue)) {
      return null;
    }
    var attribute = new ChangedAttribute();
    attribute.setAttributeName(attributeName);
    attribute.setAttributeAlias(attributeAlias);
    attribute.setOldValue(getStringValue(oldValue));
    attribute.setNewValue(getStringValue(newValue));
    return attribute;
  }

  private static String getStringValue(Object object) {
    return object != null
            ? object.toString()
            : null;
  }

  /**
   * 根据集合字段的旧值和新值，创建变更的属性信息
   */
  public static ChangedAttribute getChangedCollectionAttribute(String attributeName, String attributeAlias, Collection<?> oldValue, Collection<?> newValue) {
    var attribute = new ChangedAttribute();
    attribute.setAttributeName(attributeName);
    attribute.setAttributeAlias(attributeAlias);

    if (oldValue == null) {
      if (newValue == null) {
        return null;
      }
      else {
        attribute.setOldValue(null);
        attribute.setNewValue(getStringValue(newValue));
      }
    }
    else {
      if (newValue == null) {
        attribute.setOldValue(getStringValue(oldValue));
        attribute.setNewValue(null);
      }
      else {
        if (ListUtils.isEqualList(oldValue, newValue)) {
          return null;
        }
        attribute.setOldValue(getStringValue(oldValue));
        attribute.setNewValue(getStringValue(newValue));
      }
    }
    return attribute;
  }
}
