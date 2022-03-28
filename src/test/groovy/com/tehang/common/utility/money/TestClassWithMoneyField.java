package com.tehang.common.utility.money;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

public class TestClassWithMoneyField {

  @JsonSerialize(using = Money.Serializer.class)
  @JsonDeserialize(using = Money.Deserializer.class)
  private Money money;

  public Money getMoney() {
    return money;
  }

  public void setMoney(Money money) {
    this.money = money;
  }
}
