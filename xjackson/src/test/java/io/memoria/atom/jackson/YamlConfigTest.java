package io.memoria.atom.jackson;

import io.memoria.atom.core.text.TextException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

class YamlConfigTest {
  @Test
  @DisplayName("App config nested values should be deserialized correctly")
  void appConfig() throws IOException, TextException {
    String config = Tests.CONFIG_FILE_OPS.read("cases/config/yaml/AppConfigs.yaml");
    var appConfig = Tests.yaml.deserialize(config, AppConfig.class);
    assert appConfig != null;
    assertEquals("hello world", appConfig.subName());
    assertEquals(List.of("hi", "hello", "bye"), appConfig.subList());
  }
}
