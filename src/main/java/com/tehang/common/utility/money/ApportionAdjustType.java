package com.tehang.common.utility.money;

/**
 * 当金额分摊有余数时，将余数调整到哪一个金额上。
 */
public enum ApportionAdjustType {

  /** 当金额分摊有余数时，将余数调整到第一个金额上面 */
  ToFirst,

  /** 当金额分摊有余数时，将余数调整到最后一个金额上面 */
  ToLast
}
