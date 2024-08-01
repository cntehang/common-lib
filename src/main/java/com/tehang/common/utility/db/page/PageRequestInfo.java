package com.tehang.common.utility.db.page;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.tehang.common.utility.JsonUtils;
import com.tehang.common.utility.baseclass.DtoBase;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

/** 分页查询的请求参数。*/
@Getter
@Setter
public class PageRequestInfo extends DtoBase {

  @JsonSerialize(using = JsonUtils.LongSerializer.class)
  @ApiModelProperty(value = "当前页面的最小id值, 初次查询时传null, 用户翻页时根据上次查询结果填充该字段", dataType = "java.lang.String", example = "10001")
  private Long minId;

  @JsonSerialize(using = JsonUtils.LongSerializer.class)
  @ApiModelProperty(value = "当前页面的最大id值, 初次查询时传null, 用户翻页时根据上次查询结果填充该字段", dataType = "java.lang.String", example = "10010")
  private Long maxId;

  @ApiModelProperty(value = "分页查询方式, 初始化查询/查询下一页/查询上一页", example = "Next")
  private PageQueryMode pageQueryMode;

  @ApiModelProperty(value = "页面大小，不传默认为10", example = "10")
  private Integer pageSize;
}
