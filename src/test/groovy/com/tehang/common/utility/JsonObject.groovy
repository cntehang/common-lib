package com.tehang.common.utility

class JsonObject {

  String stringValue
  int intValue
  boolean boolValue

  boolean equals(JsonObject valueToCompare) {
    return stringValue == valueToCompare.stringValue &&
        intValue == valueToCompare.intValue &&
        boolValue == valueToCompare.boolValue
  }
}
