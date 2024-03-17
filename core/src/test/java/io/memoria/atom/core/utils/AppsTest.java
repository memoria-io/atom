package io.memoria.atom.core.utils;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

class AppsTest {

  @Test
  void appMainCase() {
    var map = Apps.readMainArgs(new String[]{"--config=path/to/file"});
    Assertions.assertEquals("path/to/file", map.get("--config"));
  }
}
