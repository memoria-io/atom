package io.memoria.atom.core.utils;

import java.util.HashMap;
import java.util.Map;

public class Apps {

  private Apps() {}

  public static Map<String, String> readMainArgs(String[] args) {
    var map = new HashMap<String, String>();
    for (String str : args) {
      var split = str.split("=");
      map.put(split[0], split[1]);
    }
    return map;
  }
}
