package com.tehang.common.utility.db;

import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.io.Serializable;

/**
 * 分页查询参数基类
 */
@Getter
@Setter
@ToString
public class PageSearchBaseDto implements Serializable {

  private static final long serialVersionUID = 7669231119625205317L;

  /**
   * 需要展示的页码
   */
  @ApiModelProperty(value = "页码，从1开始", example = "1")
  private Integer pageNumber = 1;

  /**
   * 页面大小
   */
  @ApiModelProperty(value = "页面大小，不传默认为10，建议传此值", example = "10")
  private Integer pageSize = 10;

  /**
   * 排序字段
   */
  @ApiModelProperty(value = "排序字段(默认id)，具体能传入的字段见相应接口总说明", example = "id")
  private String orderByField = "id";

  /**
   * 排序方向
   */
  @ApiModelProperty(value = "排序方向: asc, desc, 默认为desc", example = "desc")
  private String orderByDirection = "desc";
}
