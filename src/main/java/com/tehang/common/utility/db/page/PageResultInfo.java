package com.tehang.common.utility.db.page;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.tehang.common.utility.JsonUtils;
import com.tehang.common.utility.baseclass.DtoBase;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

/** 分页查询的返回信息。*/
@Getter
@Setter
public class PageResultInfo extends DtoBase {

  @JsonSerialize(using = JsonUtils.LongSerializer.class)
  @ApiModelProperty(value = "当前页面的最小id值", dataType = "java.lang.String", example = "10001")
  private Long minId;

  @JsonSerialize(using = JsonUtils.LongSerializer.class)
  @ApiModelProperty(value = "当前页面的最大id值", dataType = "java.lang.String", example = "10010")
  private Long maxId;

  @ApiModelProperty(value = "是否显示上一页按钮", example = "true", required = true)
  private Boolean previous;

  @ApiModelProperty(value = "是否显示下一页按钮", example = "true", required = true)
  private Boolean next;

  // ----------- 方法 ------------

  public static PageResultInfo of(boolean previous, boolean next, Long minId, Long maxId) {
    var result = new PageResultInfo();
    result.previous = previous;
    result.next = next;
    result.minId = minId;
    result.maxId = maxId;
    return result;
  }
}
