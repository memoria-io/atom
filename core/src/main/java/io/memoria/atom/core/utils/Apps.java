package io.memoria.atom.core.utils;


import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Apps {

  private Apps() {}

  public static Map<String, String> readMainArgs(String[] args) {
    var entries = List.of(args).map(arg -> arg.split("=")).map(arg -> Tuple.of(arg[0], arg[1]));
    return HashMap.ofEntries(entries);
  }
}
