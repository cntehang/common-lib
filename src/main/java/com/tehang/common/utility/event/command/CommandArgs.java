package com.tehang.common.utility.event.command;

import com.tehang.common.utility.baseclass.ValueObject;
import lombok.Getter;
import lombok.Setter;

/**
 * 命令参数的基类
 */
@Getter
@Setter
public abstract class CommandArgs extends ValueObject {

  protected CommandArgs() {
    // default ctor
  }
}
