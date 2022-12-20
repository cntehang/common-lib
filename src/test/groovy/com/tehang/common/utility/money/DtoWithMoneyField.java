package com.tehang.common.utility.money;

import com.tehang.common.utility.baseclass.DtoBase;

public class DtoWithMoneyField extends DtoBase {

  private Money money;

  public Money getMoney() {
    return money;
  }

  public void setMoney(Money money) {
    this.money = money;
  }
}
