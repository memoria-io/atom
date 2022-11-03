package io.memoria.atom.core.app;

import io.vavr.Tuple;
import io.vavr.collection.HashMap;
import io.vavr.collection.List;
import io.vavr.collection.Map;

public class AppUtils {

  private AppUtils() {}

  public static Map<String, String> readMainArgs(String[] args) {
    var entries = List.of(args).map(arg -> arg.split("=")).map(arg -> Tuple.of(arg[0], arg[1]));
    return HashMap.ofEntries(entries);
  }
}
