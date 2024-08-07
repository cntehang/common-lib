package com.tehang.common.utility.db.page;

import com.tehang.common.utility.baseclass.DtoBase;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

/** 分页查询请求参数的基类。*/
@Getter
@Setter
public abstract class PageRequest extends DtoBase {

  @ApiModelProperty(value = "分页查询参数, 用户翻页查询时，根据上次查询结果填充该对象，不传默认为查询第一页")
  private PageRequestInfo pageRequestInfo;

  protected PageRequest() {
    // do nothing
  }
}
