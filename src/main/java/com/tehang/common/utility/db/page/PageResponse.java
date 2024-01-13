package com.tehang.common.utility.db.page;

import com.tehang.common.utility.baseclass.DtoBase;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

/** 分页查询结果的基类。*/
@Getter
@Setter
public abstract class PageResponse<R extends IdProvider> extends DtoBase {

  @ApiModelProperty(value = "当前页的数据")
  private List<R> records;

  @ApiModelProperty(value = "分页信息，请根据此字段控制是否显示上一页，下一页按钮，并在查询时传递相关的分页参数", required = true)
  private PageResultInfo pageResultInfo;

  protected PageResponse() {
    // do nothing
  }
}
