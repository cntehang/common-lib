package com.tehang.common.utility;

import com.tehang.common.utility.baseclass.DtoBase;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 字典项数据dto
 */
@Data
@EqualsAndHashCode(callSuper = false)
public class DictItemDto extends DtoBase {

  @ApiModelProperty(value = "字典数据项代码", required = true, example = "1001")
  private String code;

  @ApiModelProperty(value = "字典数据项描述", required = true, example = "这是数据项描述")
  private String desc;

  // ---------- 方法 -----------

  public static DictItemDto of(String code, String desc) {
    var item = new DictItemDto();
    item.code = code;
    item.desc = desc;
    return item;
  }
}
