package com.tehang.common.utility.baseclass;

import com.tehang.common.utility.time.BjTime;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.Column;
import javax.persistence.MappedSuperclass;
import java.io.Serializable;

/**
 * 带有创建时间和更新时间的聚合根的基类。
 */
@Getter
@Setter
@MappedSuperclass
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@SuppressWarnings( {"PMD.GenericsNaming", "PMD.AbstractClassWithoutAnyMethod"})
public abstract class AggregateRoot<PK extends Serializable> extends AggregateRootBase<PK> {

  /**
   * 创建时间
   */
  @Column(nullable = false, length = 23)
  private BjTime createTime;

  /**
   * 更新时间
   */
  @Column(nullable = false, length = 23)
  private BjTime updateTime;

  // ------------- 方法 ------------

  /**
   * 设置createTime和updateTime为当前时间
   */
  public void resetCreateAndUpdateTimeToNow() {
    var now = BjTime.now();
    this.createTime = now;
    this.updateTime = now;
  }

  /**
   * 设置updateTime为当前时间
   */
  public void resetUpdateTimeToNow() {
    this.updateTime = BjTime.now();
  }
}
